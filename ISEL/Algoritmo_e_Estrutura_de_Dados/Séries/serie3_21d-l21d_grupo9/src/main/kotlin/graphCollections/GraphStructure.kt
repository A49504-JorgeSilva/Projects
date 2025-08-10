package series.serie3.graphCollections

class GraphStructure<I, D> : Graph<I, D> {
    data class Vertex<I, D>(override val id: I, override var data: D) : Graph.Vertex<I, D> {
        private val adjacencies: MutableSet<Graph.Edge<I>?> = mutableSetOf()

        override fun setData(newData: D): D {
            val oldData = data
            data = newData
            return oldData
        }

        override fun getAdjacencies(): MutableSet<Graph.Edge<I>?> {
            return adjacencies
        }
    }

    data class Edge<I>(
        override val id: I,
        override val adjacent: I,
        override val weight: Int
    ) : Graph.Edge<I>

    private val vertices = mutableMapOf<I, Vertex<I, D>>()
    private val edges = mutableMapOf<Pair<I, I>, Edge<I>>()

    override val size: Int
        get() = vertices.size

    override fun addVertex(id: I, d: D): D? {
        val vertex = vertices[id]
        if (vertex == null) {
            vertices[id] = Vertex(id, d)
        } else {
            vertex.data = d
        }
        return d
    }

    override fun addEdge(id: I, idAdj: I, w: Int): I? {
        val vertex1 = vertices[id] ?: Vertex(id, null as D).also { vertices[id] = it }
        //   val vertex2 = vertices[idAdj] ?: Vertex(idAdj, null as D ).also { vertices[idAdj] = it }
        val edge = Edge(id, idAdj, w)
        edges[Pair(id, idAdj)] = edge
        vertex1.getAdjacencies().add(edge)
        return idAdj
    }

    override fun getVertex(id: I): Vertex<I, D>? {
        return vertices[id]
    }

    override fun getEdge(id: I, idAdj: I): Edge<I>? {
        return edges[Pair(id, idAdj)]
    }

    override fun iterator(): Iterator<Vertex<I, D>> {
        return vertices.values.iterator()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (v in vertices.values) {
            sb.append("${v.id} -> [")
            sb.append(v.getAdjacencies().joinToString(", ") { "(${it!!.id},${it.adjacent})" })
            sb.append("]\n")
        }
        return sb.toString()
    }
}