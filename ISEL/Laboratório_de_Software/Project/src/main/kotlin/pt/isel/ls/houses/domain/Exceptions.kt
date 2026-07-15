package pt.isel.ls.houses.domain

class NotFoundException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)   //Exception usado para o email igual e locations com o nome igual
class InvalidHierarchyException(message: String) : Exception(message)
class InvalidInputException(message: String) : Exception(message)
class AuthenticationException(message: String) : Exception(message)