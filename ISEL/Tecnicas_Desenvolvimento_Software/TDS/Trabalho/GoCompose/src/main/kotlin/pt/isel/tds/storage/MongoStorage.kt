package pt.isel.tds.storage

import com.mongodb.MongoWriteException

/**
 * Storage implementation using MongoDB.
 * @param Key the type of the key.
 * @param Data the type of the data.
 */
class MongoStorage<Key,Data>(
    driver: MongoDriver,//instância do driver do MongoDB
    collectionName: String,//nome da coleção
    private val serializer: Serializer<Data>//instancia da interface Serializer que é usada para serializar e deserializar os dados antes de armazená-los no banco de dados
) : Storage<Key, Data> {

    data class Doc(val _id: String, val value: String)

    private val col = driver.getCollection<Doc>(collectionName)

    /*
    possui uma classe interna Doc que é usada para representar os documentos armazenados no banco de dados.
    Cada documento possui 2 campos: _id e value.
    O campo _id é usado para armazenar o identificador (chave) do documento.
    O campo value é usado para armazenar os dados serializados.
     */
    private fun Doc(key: Key, data: Data) =
        Doc(key.toString(), serializer.serialize(data))

    /*
    insere um novo documento no banco de dados com a chave e os dados.
    Se um documento já existir com o mm indentificador(chave) então é lançada uma exceção.
     */
    override fun create(key: Key, data: Data) {
        try { col.insertDocument( Doc(key, data) ) }
        catch (e: MongoWriteException) {
            error("Key $key already exists")
        }
    }

    /*
    recupera o documento com o identificador (chave) dado e deserializa os dados usando a instancia serializer.
    Se nenhum documento existir com o identificador dado então retorna null.
     */
    override fun read(key: Key): Data? =
        col.getDocument(key.toString())?.let { serializer.deserialize(it.value) }


    /*
    atualiza o documento com o identificador (chave) dado e serializa os dados usando a instancia serializer.
    se nenhum documento existir com o identificador dado então é lançada uma exceção.
     */
    override fun update(key: Key, data: Data) {
        check(col.replaceDocument(key.toString(), Doc(key, data))) {
            "Key $key does not exist"
        }
    }

    /*
    remove o documento com o identificador (chave) dado.
    se nenhum documento existir com o identificador dado então é lançada uma exceção.
    */
    override fun delete(key: Key) {
        check(col.deleteDocument(key.toString())) {
            "Key $key does not exist"
        }
    }
}