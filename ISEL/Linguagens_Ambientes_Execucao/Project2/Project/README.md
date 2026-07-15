# yamlify

Parser/serializador YAML → objeto Kotlin, no mesmo espírito do [autorouter](../../Project1/Project) e do [jdbcrepo](../../Project3/Project2/jdbcrepo-42d-42d-g03-main): duas estratégias de mapeamento planeadas, uma via **Kotlin Reflection** (`YamlParserReflect`) e outra via **geração de bytecode em runtime** com [Cojen Maker](https://github.com/cojen/Maker) (`YamlParserCojen`), com benchmarks JMH (`yamlify-bench`) preparados para comparar as duas abordagens.

> **Estado**: exercício **incompleto** — o corpo de `AbstractYamlParser.parseObject()`/`parseList()` e de `YamlParserCojen.buildYamlParser()` ficou por implementar (`TODO()`). Os testes e benchmarks estão escritos mas não passam no estado atual. Mantido no repositório como registo do exercício de LAE, não como biblioteca funcional.

## Autores

Diogo Cruz, Ricardo, Jorge Silva — Turma i41n, Grupo 08.

## Stack

Kotlin, Gradle (Kotlin DSL), kotlin-reflect, JMH.
