package pt.isel

import pt.isel.autorouter.*
import java.util.*

/*
movieController implementa um controlador para uma API, application program interface, de filmes.
Possio métodos anotados com AutoRoute que indicam os endpoints da API e os tipos de requisiçoes HTTP aceites.
*/
class movieController {
/*
repo é um mapa mutável que armazena os filmes em categorias.
Cada categoria é representada por uma chave do mapa, e os filmes são armazenados como listas associadas a essas chaves.
*/
    val repo = mutableMapOf(
        "infantil" to listOf(
            movie(7236, "Cinderela", 110, 3.6),
            movie(8634, "Pequena sereia", 94, 3.0),
            movie(4485, "Frozen", 108, 3.7),
            movie(5362, "Minions", 91, 4.0),
            movie(2356, "Bela e o monstro", 84, 4.4),
            movie(5342, "Barbie", 100, 2.9),
            movie(8695, "Bambi", 70, 4.1)
        ),
        "acao" to listOf(
            movie(9876, "Avengers", 182, 4.5),
            movie(4536, "John Wick", 122, 3.5),
            movie(5689, "Batman", 140, 4.0),
        )
    )

    /**
     *      Pesquisa por um filme baseado no seu titulo
     *   http://localhost:4000/movies/infantil?movie=jo
     */

/*
Este método é anotado com AutoRoute e é usado para pesquisar por um filme com base no seu título.
Usa 2 parametros de anotação: @ArRoute movies e @ArQuery movie.
O parâmetro movies é usado para indicar a categoria de filmes a pesquisar.
O parâmetro movie é usado para indicar o título do filme a pesquisar, sendo este, optional.
Primeiro verifica se o parametro movie é null, se for retorna um optional com a lista de filmes da categoria movies.
Não sendo null, filtra os filmes da categoria movies, retornando um optional com a lista de filmes filtrados.
Não havendo filmes retorna um optional.empty()
*/
    @Synchronized
    @AutoRoute(path = "/movies/{movies}")
    fun search(@ArRoute movies: String, @ArQuery movie: String?): Optional<List<movie>> {
        return repo[movies]
            ?.let {
                if(movie == null) Optional.of(it)
                else Optional.of(it.filter { st -> st.title.lowercase().contains(movie.lowercase()) })
            }
            ?: Optional.empty()
    }

    /**
     * Example:
     *      Utiliza o metodo PUT para adicionar um movie à lista de movies
     *     http://localhost:4000/movies/acao/movie/7777
     */
/*
Este método é anotado com AutoRoute e é usado para adicionar um filme à lista de filmes.
É acionado por um pedido PUT, e usa 3 parametros de anotação: @ArRoute movies, @ArRoute id e @ArBody m.
O parâmetro movies é usado para indicar a categoria de filmes a adicionar,sendo obtido diretamente da ArRoute.
O parâmetro id é usado para indicar o id do filme a adicionar,sendo obtido diretamente da ArRoute.
O parâmetro m é usado para indicar o filme a adicionar,sendo obtido diretamente da ArBody.
O método verifica se o id do filme a adicionar é igual ao id do filme recebido no corpo do pedido.
Não coincidindo retorna um Optional.empty(). Caso contrário, obtém a lista de filmes da categoria movies.
O método substitui a lista de filmes da categoria movies pela lista de filmes filtrada, adicionando o filme m.
*/
    @Synchronized
    @AutoRoute(path = "/movies/{movies}/movie/{id}", method = ArVerb.PUT)
    fun addMovie(
        @ArRoute movies: String,
        @ArRoute id: Int,
        @ArBody m: movie
    ): Optional<movie> {
        if(id != m.id) return Optional.empty()
        val stds = repo[movies] ?: emptyList()
        repo[movies] = stds.filter { it.id != id } + m
        return Optional.of(m)
    }

    /**
     *      Utiliza o metodo DELETE para retirar um movie da lista de movies
     *    http://localhost:4000/movies/acao/movie/4536
     */

/*
Este método é anotado com AutoRoute e é usado para remover um filme da lista de filmes.
É acionado por um pedido DELETE, e usa 2 parametros de anotação: @ArRoute movies e @ArRoute id.
O parâmetro movies é usado para indicar a categoria a remover,sendo obtido diretamente da ArRoute.
O parâmetro id é usado para indicar o id do filme a remover,sendo obtido diretamente da ArRoute.
Primeiro, verifica se a categoria movies existe no mapa repo, se não existir retorna um Optional.empty().
Depois, verifica se há um filme na categoria com o id fornecido, se não houver retorna um Optional.empty().
Caso contrário, obtém a lista de filmes da categoria movies, e filtra a lista de filmes, removendo o filme com o id fornecido.
*/
    @Synchronized
    @AutoRoute(path = "/movies/{movies}/movie/{id}", method = ArVerb.DELETE)
    fun removeMovie(@ArRoute movies: String, @ArRoute id: Int) : Optional<movie> {
        val stds = repo[movies] ?: return Optional.empty()
        val s = stds.firstOrNull { it.id == id } ?: return Optional.empty()
        repo[movies] = stds.filter { it.id != id }
        return Optional.of(s)
    }
}