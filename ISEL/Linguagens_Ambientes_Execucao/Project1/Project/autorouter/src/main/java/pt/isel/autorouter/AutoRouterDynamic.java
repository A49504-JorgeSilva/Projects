package pt.isel.autorouter;

import kotlin.jvm.functions.Function2;
import org.cojen.maker.ClassMaker;
import org.cojen.maker.MethodMaker;
import org.cojen.maker.Variable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class AutoRouterDynamic {
// este método recebe um objeto controller e retorna um stream de objetos ArHttpRoute
    public static Stream<ArHttpRoute> autorouterDynamic(Object controller) {
/*
Obtém os métodos do controller e filtra-os para selecionar
apenas aqueles que possuem a anotação @AutoRoute e retornam optional.
*/
        Method[] methods = controller.getClass().getMethods();
        Stream<Method> methodStream = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(AutoRoute.class))
                .filter(method -> method.getReturnType() == Optional.class);
/*
Mapeia cada método para um ArHttpRoute.
O objeto ArHttpRoute é construído através do método buildHandler para gerar a classe do manipulador dinâmico.
Em seguida o construtor da classe é invocado para criar uma instância do manipulador.
O objeto ArHttpRoute é construído com o nome do método, o verbo http, o path e o manipulador.
*/
        return methodStream.map(method -> {
            AutoRoute annotation = method.getAnnotation(AutoRoute.class);
            String path = annotation.path();
            ArVerb m = annotation.method();
            try{
                ClassMaker cm = buildHandler(controller.getClass(), method);
                Class<?> routerClass = cm.finish();
                Constructor<?> constructor = routerClass.getConstructor(controller.getClass());
                ArHttpHandler handler = (ArHttpHandler) constructor.newInstance(controller);
                return new ArHttpRoute(method.getName(), m, path, handler);
            }
            catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

// este método gera dinamicamente a classe do manipulador HTTP com base nas informações do controlador e do método.
    public static ClassMaker buildHandler(Class<?> routerClass, Method fun) {
//cria uma nova instância de ClassMaker. O classMaker é uma classe do Cojen que permite gerar classes dinamicamente.
        ClassMaker cm = ClassMaker.begin().public_().implement(ArHttpHandler.class);
//adiciona um campo ao manipulador para guardar a instância do controlador.
        //private final ClassroomController controller;
        cm.addField(routerClass, "controller");
//adiciona um construtor ao manipulador que recebe uma instância do controlador e guarda-a no campo controller.
        //public SearchArHttpHandler(ClassroomController controller) {
        //super();
        //this.controller = controller;}
        MethodMaker m = cm.addConstructor(routerClass).public_();
        m.invokeSuperConstructor();
        m.field("controller").set(m.param(0));
//adiciona o método handle ao manipulador responsável por lidar com as requisições HTTP usando cm.addMethod.
        //public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs)
        MethodMaker mm = cm.addMethod(Optional.class, "handle", Map.class, Map.class, Map.class).public_();
//obtém os parâmetros do método e converte-os para String.
        Variable[] params = getParams(mm, fun);

//chama o método do controlador com os parâmetros convertidos usando mm.field("controller").invoke(fun.getName(), (Object[]) params);
        //controller.search(routeArgs.get("classroom"), queryArgs.get("student"));
        var methodRet = mm.field("controller").invoke(fun.getName(), (Object[]) params);


//retorna o resultado do método original
        //return
        mm.return_(methodRet);

        return cm;
    }

//este método obtém os parâmetros do método e converte-os para uma matriz de variáveis.
    private static Variable[] getParams(MethodMaker mm, Method fun) {
/*
Obtém os parâmetros do método usando getParameters.
Itera sobre os parâmetros e verifica se possuem a anotação @ArRoute, @ArQuery ou @ArBody.
Obtém o valor do parâmetro da solicitação coorrespondete com base na anotação.
Converte o valor para o tipo do parâmetro usando getConvertedParams.
Retorna a matriz de variáveis.
 */
        Parameter[] params = fun.getParameters();
        Variable[] stringParams = new Variable[params.length];
        for (int i = 0; i < params.length; i++) {
            String paramName = params[i].getName();
            Class<?> paramType = params[i].getType();
            if (params[i].isAnnotationPresent(ArRoute.class)) {
                Variable value = mm.param(0).invoke("get", paramName).cast(String.class);
                stringParams[i] = getVariable(value, paramType, mm,0);
            } else if (params[i].isAnnotationPresent(ArQuery.class)) {
                Variable value = mm.param(1).invoke("get", paramName).cast(String.class);
                stringParams[i] = getVariable(value, paramType, mm,1);
            } else if (params[i].isAnnotationPresent(ArBody.class)) {
                Variable value = mm.param(2).invoke("get", paramName).cast(String.class);
                stringParams[i] = getVariable(value, paramType, mm,2);
            }
        }
        return stringParams;
    }
/*
Métodos auxiliares getConvertedParams, createVariableFromParamValues e convert são usados para converter
os parâmetros em seus tipos esperados. Eles lidam com a conversão de tipos primitivos e tipos de classe.
O método createVariableFromParamValues é usado para criar um objeto a partir dos valores dos parâmetros usando o construtor.
*/
    private static Variable getConvertedParams(Variable value, Class<?> paramType, MethodMaker mm) {
        if (paramType == String.class) {
            return value;
        } else {
            return typeConversion.get(paramType).invoke(value, mm);
        }
    }

    private static Variable getVariable (Variable value, Class<?> paramType, MethodMaker mm, int idx){
        if (paramType == String.class || paramType.isPrimitive()){
            return getConvertedParams(value, paramType, mm);
        } else {
            return createVariableFromParamValues(mm, paramType, idx);
        }
    }
/*
Esta variável é um mapa que mapeia os tipos de parâmetros para funções que convertem os valores dos parâmetros em seus tipos esperados.
Cada tipo tem uma função associada que realiza a conversão correta para o tipo desejado.
Por exemplo, o tipo In é mapeado para uma função que converte o valor do parâmetro para um Integer usando o método Integer.parseInt.
*/
    private static final Map<Class<?>, Function2<Variable, MethodMaker, Variable>> typeConversion = new HashMap<>();

    static {
        typeConversion.put(int.class, (s, mm) -> convert(Integer.class, "parseInt", s, mm));
        typeConversion.put(double.class, (s, mm) -> convert(Double.class, "parseDouble", s, mm));
        typeConversion.put(float.class, (s, mm) -> convert(Float.class, "parseFloat", s, mm));
        typeConversion.put(long.class, (s, mm) -> convert(Long.class, "parseLong", s, mm));
        typeConversion.put(short.class, (s, mm) -> convert(Short.class, "parseShort", s, mm));
        typeConversion.put(byte.class, (s, mm) -> convert(Byte.class, "parseByte", s, mm));
        typeConversion.put(boolean.class, (s, mm) -> convert(Boolean.class, "parseBoolean", s, mm));
        typeConversion.put(char.class, (s, mm) -> s.invoke("charAt", 0));
    }

    private static Variable convert(Class<?> clazz, String funName, Variable param, MethodMaker mm) {
        return mm.var(clazz).invoke(funName, param);
    }


    private static Variable createVariableFromParamValues(MethodMaker mm, Class<?> objectClass,int idx) {
        try {
            //Aqui vamos ver se só existe 1 construtor pois só pode ter 1
            if (objectClass.getConstructors().length != 1) {
                throw new Exception("Object class " + objectClass.getName() + " must have exactly one constructor.");
            }
            Constructor<?> constructor = objectClass.getConstructors()[0];

            //criar um array onde se vai meter os valores convertidos
            Variable[] args = new Variable[constructor.getParameterCount()];

            //um array com todos os paramentros da class Student ou outra class feita por nós
            Parameter[] parameters = constructor.getParameters();
            //while para meter no array arg os parametros convertidos
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Variable paramValue = mm.param(idx).invoke("get", parameter.getName()).cast(String.class);
                try {
                    args[i] = getConvertedParams(paramValue, parameter.getType(), mm);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get argument for parameter " + parameter.getName() + " of type "
                            + parameter.getType().getSimpleName(), e);
                }
            }
            //retornar o objecto criado
            return mm.new_(objectClass, (Object[]) args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create object of class " + objectClass.getName(), e);
        }
    }
}
