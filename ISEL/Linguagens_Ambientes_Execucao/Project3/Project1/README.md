[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Uuau0Rla)
[![Work in MakeCode](https://classroom.github.com/assets/work-in-make-code-8824cc13a1a3f34ffcd245c82f0ae96fdae6b7d554b6539aec3a03a70825519c.svg)](https://classroom.github.com/online_ide?assignment_repo_id=18250882&assignment_repo_type=AssignmentRepo)
## jsonoy - JSON Encoder

JSON Encoder through Reflection API.

## DEADLINES

1. **03-03-2025**
1. **10-03-2025**

***

Use this project as the template for your implementation of a JSON Encoder.

**You are only allowed to use AI tools, such as Copilot, ChatGPT, or others, for
requirement 2.2.**  
The rest of this assignment must be completed independently, as it is the only
way to fully grasp the use and purpose of Reflection.  

Remember: "_Programming is understanding._" — Kristen Nygaard.

## 1. `fieldsToJson` 

Implement the extension function `fieldsToJson` (in `FieldsToJson.kt`), which
converts the declared fields of a given object to JSON. This function should use
the Java Reflection API to inspect fields and recursively process non-primitive
types.

Your implementation should pass both `testAddressFieldsToJson()` and
`testClassroomFieldsToJson()` tests.

## 2. `membersToJson`

Implement the extension function `membersToJson` (in `MembersToJson.kt`) using
the Kotlin Reflection API.  
This function encodes properties and non-void parameterless functions.  
The `membersToJson` function must be recursive.

- Members can be annotated with `ToJsonPropName` to replace the member's name
  with the specified JSON property name.
- Members can also be annotated with `ToJsonFormatter`, which specifies a class
  implementing a function `(Any) -> String`. This function provides an
  alternative JSON representation of the member's value, processing it.

1. Your implementation should pass both `testClassroomMembersToJson()`  and
`testPersonMembersToJson()` tests. These tests utilize the domain model
according to the following design:

<img src="https://www.mermaidchart.com/raw/6452bf07-f101-479d-a9ad-ce7bb51d8ab6?theme=light&version=v0.1&format=svg" width="400">

2. Implement unit tests for a domain model of your choice that includes at least
   two associations, with one having a multiple multiplicity relationship.
   You may use any AI tool, such as Copilot, ChatGPT, or others, to assist you
   in implementing these unit tests and the domain model.