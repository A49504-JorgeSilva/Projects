package pt.isel.tds.go.model

/**
 * Represents a group of positions of a stone type.
 * @property positions The group positions.
 * @property stone The stone.
 */
data class Group(val positions: Set<Position>, val stone: Stone)

/**
 * Adds a position to a group.
 * @param moves The board moves.
 * @param position The position to add.
 * @param groups The groups.
 * @param stone The stone.
 * @return The new group.
 */
fun addToGroup(moves: Moves, position: Position, groups: Set<Group>, stone: Stone): Group {
    val row = position.rowIndex
    val col = position.colIndex
    val match: Set<Position> = setOfNotNull(
        getSurrounded(Position(row + 1, col)) {
            row + 1 < BOARD_SIZE && moves[Position(row + 1, col)] == stone
        },
        getSurrounded(Position(row - 1, col)) {
            row - 1 >= 0 && moves[Position(row - 1, col)] == stone
        },
        getSurrounded(Position(row, col + 1)) {
            col + 1 < BOARD_SIZE && moves[Position(row, col + 1)] == stone
        },
        getSurrounded(Position(row, col - 1)) {
            col - 1 >= 0 && moves[Position(row, col - 1)] == stone
        }
    )
    return if (match.isEmpty()) {
        Group(setOf(position), stone)
    } else {
        val flatGroups = match.flatMap { pos ->
            groups.find { it.positions.contains(pos) }?.positions.orEmpty()
        }.toSet()

        Group(flatGroups.plus(position).toSet(), stone)

    }
}

/**
 * Checks if the group is captured.
 * @param moves The board moves.
 * @return True if the group is captured, false otherwise.
 */
fun Group.isGroupCaptured(moves: Moves): Boolean {
    for (position in this.positions) {
        if (!position.isCaptured(moves)) {
            return false
        }
    }
    return true
}