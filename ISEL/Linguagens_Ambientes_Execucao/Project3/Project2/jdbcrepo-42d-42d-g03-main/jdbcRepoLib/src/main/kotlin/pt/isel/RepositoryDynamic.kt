package pt.isel

import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Class
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_String
import java.lang.constant.ConstantDescs.CD_boolean
import java.lang.constant.ConstantDescs.CD_char
import java.lang.constant.ConstantDescs.CD_double
import java.lang.constant.ConstantDescs.CD_float
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.CD_long
import java.lang.constant.ConstantDescs.CD_short
import java.lang.constant.ConstantDescs.CD_void
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.MethodTypeDesc
import java.net.URLClassLoader
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotations

/**
 * Package name where the dynamic classes will be generated.
 */
private const val PACKAGE_NAME: String = "pt.isel"

/**
 * Path of the package corresponding to the package name.
 */
private val packageFolder = PACKAGE_NAME.replace(".", "/")

/**
 * Root directory of the project, used to locate the classpath.
 */
private val root =
    RepositoryReflect::class.java
        .getResource("/")
        ?.toURI()
        ?.path
        ?: "${System.getProperty("user.dir")}/"

/**
 * ClassLoader used to load dynamically generated classes.
 */
private val rootLoader = URLClassLoader(arrayOf(File(root).toURI().toURL()))

/**
 * Cache of already created dynamic repositories, mapped by the domain class.
 */
private val repositories = mutableMapOf<KClass<*>, RepositoryReflect<*, *>>()

/**
 * Loads or dynamically creates a repository for the provided domain class.
 *
 * @param K The type of the primary key.
 * @param T The type of the domain entity.
 * @param connection The database connection.
 * @param domainKlass The domain class for which the repository will be created.
 * @return An instance of `RepositoryReflect` for the provided domain class.
 */
fun <K : Any, T : Any> loadDynamicRepo(
    connection: Connection,
    domainKlass: Class<T>,
) = loadDynamicRepo<K, T>(connection, domainKlass.kotlin)

/**
 * Loads or dynamically creates a repository for the provided domain class.
 *
 * @param K The type of the primary key.
 * @param T The type of the domain entity.
 * @param connection The database connection.
 * @param domainKlass The domain class for which the repository will be created.
 * @return An instance of `RepositoryReflect` for the provided domain class.
 */
fun <K : Any, T : Any> loadDynamicRepo(
    connection: Connection,
    domainKlass: KClass<T>,
) = repositories.getOrPut(domainKlass) {
    buildRepositoryClassfile(domainKlass)
        .constructors
        .first()
        .call(connection) as RepositoryReflect<*, *>
} as RepositoryReflect<K, T>

/**
 * Generates and dynamically loads a class file for a repository based on the provided domain class.
 *
 * @param T The type of the domain entity.
 * @param domainKlass The domain class for which the repository will be generated.
 * @return The dynamically generated class as a `KClass`.
 */
private fun <T : Any> buildRepositoryClassfile(domainKlass: KClass<T>): KClass<out Any> {
    val className = "RepositoryDyn${domainKlass.simpleName}"
    buildRepositoryByteArray(className, domainKlass)
    return rootLoader
        .loadClass("$PACKAGE_NAME.$className")
        .kotlin
}

/**
 * Generates a byte array representing a dynamically created
 * class that extends RepositoryReflect, and then saves it to the
 * corresponding class file.
 * @param T The type of the domain entity.
 * @param className Name of the class to be generated.
 * @param domainKlass The domain class for which the repository will be generated.
 */
fun <T : Any> buildRepositoryByteArray(
    className: String,
    domainKlass: KClass<T>,
) {
    val fullClassName = "$PACKAGE_NAME.$className"
    val classDesc = ClassDesc.of(fullClassName)
    val repositoryReflectDesc = ClassDesc.of(RepositoryReflect::class.java.name)
    val connectionDesc = ClassDesc.of(Connection::class.java.name)
    val kClassDesc = ClassDesc.of(KClass::class.java.name)
    val resultSetDesc = ClassDesc.of("java.sql.ResultSet")

    val bytes: ByteArray =
        ClassFile
            .of()
            .build(classDesc) { clb ->
                clb.withFlags(ACC_PUBLIC)
                clb.withSuperclass(repositoryReflectDesc)
                clb.withMethod(
                    INIT_NAME,
                    MethodTypeDesc.of(CD_void, connectionDesc),
                    ACC_PUBLIC,
                ) { mb ->
                    mb.withCode { cb ->
                        cb
                            .aload(0)
                            .aload(1)
                            .ldc(ClassDesc.of(domainKlass.java.name))
                            .invokestatic(
                                ClassDesc.of("kotlin.jvm.JvmClassMappingKt"),
                                "getKotlinClass",
                                MethodTypeDesc.of(ClassDesc.of("kotlin.reflect.KClass"), CD_Class),
                            ).invokespecial(
                                repositoryReflectDesc,
                                INIT_NAME,
                                MethodTypeDesc.of(CD_void, connectionDesc, kClassDesc),
                            ).return_()
                    }
                }

                clb.withMethod(
                    "mapResultSetToEntity",
                    MethodTypeDesc.of(ClassDesc.of(domainKlass.java.name), resultSetDesc),
                    // MethodTypeDesc.of(CD_Object, resultSetDesc),
                    ACC_PUBLIC,
                ) { mb ->
                    mb.withCode { cb ->
                        cb.new_(domainKlass.descriptor())
                        cb.dup()

                        domainKlass.constructors.first().parameters.forEachIndexed { index, param ->
                            val columnAnnotation = param.findAnnotations<Column>().firstOrNull()
                            val columnName = columnAnnotation?.name ?: param.name
                            val typeDesc = param.type.descriptor()
                            val kClass =
                                param.type.classifier as? KClass<*>
                                    ?: error("Unsupported type: ${param.type}")

                            if (kClass.annotations.any { it is Table }) {
                                cb.aload(0)
                                cb.aload(1)
                                cb.ldc(clb.constantPool().stringEntry(columnName))
                                cb.invokeinterface(resultSetDesc, "getObject", MethodTypeDesc.of(CD_Object, CD_String))
                                cb.checkcast(ClassDesc.of(kClass.java.name))
                                cb.aload(0)
                                cb.invokestatic(
                                    ClassDesc.of("pt.isel.RepositoryDynamicKt"),
                                    "loadAssociation",
                                    MethodTypeDesc.of(
                                        ClassDesc.of(kClass.java.name),
                                        ClassDesc.of(Connection::class.java.name),
                                        CD_Object,
                                        ClassDesc.of(KClass::class.java.name),
                                    ),
                                )
                            } else if (kClass.java.isEnum) {
                                cb.aload(1)
                                cb.ldc(clb.constantPool().stringEntry(columnName))
                                cb.invokeinterface(resultSetDesc, "getString", MethodTypeDesc.of(CD_String, CD_String))
                                cb.invokestatic(
                                    ClassDesc.of(kClass.java.name),
                                    "valueOf",
                                    MethodTypeDesc.of(ClassDesc.of(kClass.java.name), CD_String),
                                )
                            } else {
                                val methodName = kClass.toResultSetMethodName()
                                cb.aload(1)

                                cb.ldc(clb.constantPool().stringEntry(columnName))

                                cb.invokeinterface(resultSetDesc, methodName, MethodTypeDesc.of(typeDesc, CD_String))

                                if (methodName == "getString" && typeDesc == CD_char) {
                                    cb.invokevirtual(CD_String, "charAt", MethodTypeDesc.of(CD_char, CD_int))
                                }
                            }
                        }

                        val constructorDesc =
                            domainKlass.constructors
                                .first()
                                .parameters
                                .map { it.type.descriptor() }
                        cb.invokespecial(
                            domainKlass.descriptor(),
                            INIT_NAME,
                            MethodTypeDesc.of(CD_void, *constructorDesc.toTypedArray()),
                        )

                        cb.areturn()
                    }
                }
            }

    File("$root/$packageFolder/$className.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}

fun <K : Any, T : Any> loadAssociation(
    connection: Connection,
    foreignKey: K,
    foreignClass: KClass<T>,
): T? {
    val repository = loadDynamicRepo<K, T>(connection, foreignClass)
    return repository.getById(foreignKey)
}

fun KClass<*>.toResultSetMethodName(): String {
    println("Handling type: ${this.java.name}")
    if (this.java.isEnum) return "getString"
    return when (this.java.name) {
        "int" -> "getInt"
        "long" -> "getLong"
        "double" -> "getDouble"
        "float" -> "getFloat"
        "boolean" -> "getBoolean"
        "String", "java.lang.String" -> "getString"
        "char" -> "getString"
        "Date", "java.sql.Date" -> "getDate"
        else -> error("Unsupported type: ${this.java.name}")
    }
}

/**
 * Returns a ClassDesc of the type descriptor of the given KClass.
 * @receiver The kotlin type for which the descriptor will be generated.
 * @return A `ClassDesc` representing the type of the class.
 */
fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        when (this) {
            Char::class -> CD_char
            Short::class -> CD_short
            Int::class -> CD_int
            Long::class -> CD_long
            Float::class -> CD_float
            Double::class -> CD_double
            Boolean::class -> CD_boolean
            else -> {
                throw IllegalStateException("No primitive type for ${this.qualifiedName}!")
            }
        }
    } else {
        ClassDesc.of(this.java.name)
    }

/**
 * Returns a ClassDesc of the type descriptor of the given KType.
 *
 * @receiver The kotlin type for which the descriptor will be generated.
 * @return A `ClassDesc` representing the type of the class.
 */
fun KType.descriptor(): ClassDesc {
    val klass = this.classifier as KClass<*>
    return klass.descriptor()
}
