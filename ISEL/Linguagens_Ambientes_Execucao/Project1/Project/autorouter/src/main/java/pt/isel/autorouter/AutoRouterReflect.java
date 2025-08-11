package pt.isel.autorouter;

import kotlin.jvm.functions.Function3;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class AutoRouterReflect {
/*
Esta variável é um mapa que mapeia os tipos de parâmetros para funções que convertem os valores dos parâmetros em seus tipos esperados.
Cada tipo tem uma função associada que realiza a conversão correta para o tipo desejado.
Por exemplo, o tipo In é mapeado para uma função que converte o valor do parâmetro para um Integer usando o método Integer.parseInt.
*/
    private static final Map<Class<?>, Function<String, ?>> typeConversion = new HashMap<>();

    static {
        typeConversion.put(int.class, Integer::parseInt);
        typeConversion.put(double.class, Double::parseDouble);
        typeConversion.put(float.class, Float::parseFloat);
        typeConversion.put(long.class, Long::parseLong);
        typeConversion.put(boolean.class, Boolean::parseBoolean);
        typeConversion.put(byte.class, Byte::parseByte);
        typeConversion.put(short.class, Short::parseShort);
        typeConversion.put(char.class, s -> s.charAt(0));
    }

/*
O método autorouterReflect recebe um objeto que representa um controlador e retorna uma stream de objetos ArHttpRoute.
Segue uma abordagem baseada em reflexão para criar manipuladores HTTP com base nas anotações fornecidas.
*/
    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {
/*
Obtém todos os métodos do controlador e filtra-os para selecionar apenas aqueles que têm a anotação AutoRoute e retorna um Optional.
*/
        Method[] methods = controller.getClass().getMethods();
        Stream<Method> methodStream = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(AutoRoute.class))
                .filter(method -> method.getReturnType() == Optional.class);
/*
Mapeia cada método para um objeto ArHttpRoute correspondente.
*/
        return methodStream.map(method -> {
            AutoRoute annotation = method.getAnnotation(AutoRoute.class);
            String path = annotation.path();
            ArVerb m = annotation.method();
            /*
            Ideia para remover o codigo de reflection do autorouter:
            Array com os params do metodo
            [arRoute = "id", arQuery = "age"]
            Array com as funçoes que vai retornar o objeto
            [(routeArgs, queryArgs, bodyArgs),(routeArgs, queryArgs, bodyArgs)] o array após a função retornar [1,20]
            */
/*
Para cada método cria um array de funções Functions que representam os parâmetros do método e as suas correspondentes
operações de obtenção de valores.
*/
            int nParams = method.getParameterCount();
            Function3<Map<String, String>, Map<String, String>, Map<String, String>, Object>[] functions = new Function3[nParams];
/*
Dentro do loop de parâmetros do método, cada parâmetro é inspecionado para determinar se é um ArRoute, um ArQuery ou um ArBody.
Com base na anotação, atribuí uma função no array Functions que obtém o valor do parâmetro correto e retonar o objeto convertido.
 */
            int idx = 0;
            for (var p : method.getParameters()) {
                Class<?> paramType = p.getType();
                String name = p.getName();
                if (p.isAnnotationPresent(ArRoute.class)) {
                    functions[idx] = (routeArgs, queryArgs, bodyArgs) -> {
                        String value = routeArgs.get(name);
                        return getObject(routeArgs, paramType, value, paramType);
                    };
                } else if (p.isAnnotationPresent(ArBody.class)) {
                    functions[idx] = (routeArgs, queryArgs, bodyArgs) -> {
                        String value = bodyArgs.get(name);
                        return getObject(bodyArgs, paramType, value, paramType);
                    };
                } else if (p.isAnnotationPresent(ArQuery.class)) {
                    functions[idx] = (routeArgs, queryArgs, bodyArgs) -> {
                        String value = queryArgs.get(name);
                        return getObject(queryArgs, paramType, value, paramType);
                    };
                }
                idx++;
            }
/*
Cria um novo objeto ArHttpRoute com o nome do método, o método em si, o caminho e uma função que
invoca o método com os parâmetros corretos.
No metodo handle do ArHttpRoute, cria um array de argumentos args para invocar o método, chamando as funções
correspondentes para obter os valores dos parâmetros.
Invoca o método apartir do functions[i].invoke(routeArgs, queryArgs, bodyArgs) invocando o método original
no controlador com os argumentos corretos.
*/
            return new ArHttpRoute(method.getName(), m, path, (routeArgs, queryArgs, bodyArgs) -> {
                Object[] args = new Object[nParams];
                for (int i = 0; i < nParams; i++) {
                    args[i] = functions[i].invoke(routeArgs, queryArgs, bodyArgs);
                }
                try {
                    return (Optional<?>) method.invoke(controller, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to invoke method " + method.getName() + " on controller " + controller.getClass().getName(), e);
                }

            });
        });
    }
/*
Os métodos auxiliares getConvertedParams, createObjectFromParamValues e getObject são usados para a conversão de
valores de parâmetrps e criação de objetos a partir de valores de parâmetros. A função getObject possui uma cache
objectCache para evitar criar objetos duplicados com os mesmos valores de parâmetros.
*/


    /*
     * pega nos parametros e converte-os para os seus devidos tipos e mete no array para mandar para o invoke
     */
    private static Object getConvertedParams(String value, Class<?> paramType) {
        if (value != null) {
            if (paramType == String.class) {
                return value;
            } else {
                return typeConversion.get(paramType).apply(value); //GERAR CODIGO
            }
        } else return null;
    }

    private static Object createObjectFromParamValues(Map<String, String> paramValues, Class<?> objectClass) {
        try {

            //Aqui vamos ver se só existe 1 construtor pois só pode ter 1
            if (objectClass.getConstructors().length != 1) {
                throw new Exception("Object class " + objectClass.getName() + " must have exactly one constructor.");
            }
            Constructor<?> constructor = objectClass.getConstructors()[0];

            //criar um array onde se vai meter os valores convertidos
            Object[] args = new Object[constructor.getParameterCount()];

            //um array com todos os paramentros da class Student ou outra class feita por nós
            Parameter[] parameters = constructor.getParameters();
            //while para meter no array arg os parametros convertidos
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                String value = paramValues.get(parameter.getName());
                try {
                    args[i] = getConvertedParams(value, parameter.getType());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get argument for parameter " + parameter.getName() + " of type "
                            + parameter.getType().getSimpleName(), e);
                }
            }
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create object of class " + objectClass.getName(), e);
        }
    }

    private static final Map<Integer, Object> objectCache = new HashMap<>();

    private static Object getObject(Map<String, String> paramValues, Class<?> objectClass, String value, Class<?> paramType) {
        if (paramType == String.class || paramType.isPrimitive()) {
            return getConvertedParams(value, paramType);
        }

        int hashCode = Objects.hash(objectClass, paramValues);
        if (objectCache.containsKey(hashCode)) {
            return objectCache.get(hashCode);
        }

        try {
            Object obj = createObjectFromParamValues(paramValues, objectClass);
            objectCache.put(hashCode, obj);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create object of class " + objectClass.getName(), e);
        }
    }
}