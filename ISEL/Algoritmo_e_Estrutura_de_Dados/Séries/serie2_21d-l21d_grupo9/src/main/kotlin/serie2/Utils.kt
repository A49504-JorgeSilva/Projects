package serie2

import java.util.Stack

fun verifyPairing(s: String): Boolean {
   val stack = Stack<Char>()

   for (i in s){
      if(!i.isBracket())
         continue
      if(i.isOpenBracket()) stack.push(i)
      if(stack.isEmpty() && i.isClosedBracket()) return false
      if(stack.isNotEmpty() && i.isClosedBracket()  && i.matchesBracket(stack.peek())) stack.pop()
   }
   return stack.isEmpty()
}

fun Char.isOpenBracket():Boolean{
   return this == '(' || this == '{' || this == '['  //|| this == ')' || this == '}' || this == ']'
}

fun Char.isBracket()=isOpenBracket() || isClosedBracket()

fun Char.isClosedBracket():Boolean{
   return  this == ')' || this == '}' || this == ']'
}
fun Char.matchesBracket(item: Char): Boolean{
   if (this == ')' && item == '(') return true
   if (this == ']' && item == '[') return true
   if (this == '}' && item == '{') return true
   return false
}