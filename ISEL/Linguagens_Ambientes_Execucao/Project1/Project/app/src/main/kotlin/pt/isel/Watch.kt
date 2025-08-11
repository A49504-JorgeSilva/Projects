package pt.isel

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.*

/*
Extensão para a classe Path que permite monitorizar a criação e modificação de ficheiros.
A função sequence cria uma lazy sequence que permite gerar os resultados sob demanda à medida
 que são necessários em vez de gerá-los todos duma vez.
 */
fun Path.watchNewFilesContent(): Sequence<Sequence<String>> = sequence {
/*
Cria um WatchService que permite monitorizar alterações no sistema de ficheiros através do fileSystem.newWatchService.
Regista o serviço para monitorizar a criação e modificação de ficheiros.
 */
    val service = fileSystem.newWatchService()
    register(service, ENTRY_CREATE, ENTRY_MODIFY)
/*
Este loop é executado indefinidamente para esperar por eventos de alteração nos arquivos.
Quando um evento ocorre, o WatchKey, key, correspondente é recuperado usando o método pollEvents.
O método pollEvents retorna uma lista de WatchEvent, que contém o tipo de evento e o nome do arquivo.
O tipo de evento é obtido usando o método kind.
Se o tipo do evento for ENTRY_CREAT ou ENTRY_MODIFY, um novo arquivo foi criado ou modificado. Neste caso,o conteúdo é lido.
Primeiro é construído o caminho completo do arquivo usando o método Paths.ge().
O conteúdo é lido usando fullPath.toFile().bufferedReader().lineSequence() que retorna uma sequence de linhas.
O conteúdo é retornado usando yield, que significa q a lazy sequence produzirá o conteúdo como resultado.
Se ocorrer erro lança Exception.
Após processar todos os eventos no WatchKey, o método reset é chamado para reiniciar o WatchKey.
 */
    while (true) {
        val key = service.take()
        for (watchEvent in key.pollEvents()) {
            val path = watchEvent.context() as Path
            val fullPath = Paths.get((this@watchNewFilesContent).toString(), path.toString())
            when (watchEvent.kind()) {
                OVERFLOW, ENTRY_DELETE -> continue
                ENTRY_CREATE, ENTRY_MODIFY -> {
                    try {
                        val content = fullPath.toFile().bufferedReader().lineSequence()
                        yield(content)
                    }catch (e: Exception) {
                        println("Erro: ${e.message}")
                    }
                }
            }
        }

        if (!key.reset()) {
            break
        }
    }
}