
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.Calendar;

import oursource.comparacoes.*; 

/**
   Classe Pessoa.
   Esta classe implementa a interface Serializable de forma a
   permitir escrever instancias suas em ficheiro. 
 */
public class Person implements java.io.Serializable, Comparable  {
      
   /* Atributos que não podem variar numa pessoa. */
   private String name;
   private Calendar birthDay;
   /* Atributos susceptiveis de serem alterados.  */  
   private String address;
   private Contact contact;       

  /**
      Classe interna para implementar o critério alternativo 
      de comparação por idades.
   */ 
   public static class CompareByAge extends Compare {       

      public int compare( Object firstObject, Object secondObject ) {
         /* Obter as datas de nascimento das duas pessoas. */
         Calendar firstDate = ((Person)firstObject).getBirthDay();           
         Calendar secondDate = ((Person)secondObject).getBirthDay();           
               
         /* Testar a data mais recente. */
         if(firstDate.after(secondDate)) 
            /* Uma data mais recente é maior que uma data anterior. */
            return CompareConstants.BIGGER;
         return CompareConstants.SMALLER;
      }
   }  

  /**
      Méodo estático privado que compara dois nomes independentemente 
      do número de espaços brancos entres os nomes,
   */
   private static boolean compareNames( String nameFirstPerson, String nameSecondPerson ) {              

      /* Criar os Tokenizer's com o separador "  " */
      StringTokenizer st1 = new StringTokenizer(nameFirstPerson,"  ");
      StringTokenizer st2 = new StringTokenizer(nameSecondPerson,"  ");                
                
      /* Se nao têm o mesmo numero de "nomes", então são 
         nomes de pessoas diferentes. */
      if(st1.countTokens()!=st2.countTokens())
         return false;

      /* Ambas os nomes completos têm o mesmo número de "nomes". */
      /* Comparar os sucessivos "nomes" (tokens).  */
      while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
         if(st1.nextToken().equalsIgnoreCase(st2.nextToken())==false )
            return false;
      }                     
      /* Todos os nomes parciais são iguais, logo trata-se da mesma pessoa. */
      return true;                        
   }

  /** 
      Construtor para iniciar um objecto Person.
   */
   public Person( String fullName, Calendar birthDay, String address, Contact contact ) {

      this.name = fullName; 
      this.birthDay  = birthDay;
      this.address   = address;
      this.contact = contact;
   }

  /**
      Obter o primeiro nome.
   */
   public String getFirstName()   { 
      /* Criar o Tokenizer com o separador "  " sobre o nome. */
      StringTokenizer st = new StringTokenizer(name," ");
      /* O primeiro token é o primeioro nome da pessoa. */
      return st.nextToken();
   }                

  /**
      Obter o último nome.
   */
   public String getLastName()    {       
      /* Criar o Tokenizer com o separador " " sobre o nome. */
      StringTokenizer st = new StringTokenizer(name,"  ");
      /* Verificar o número de nomes. */                
      if(st.countTokens()==1) return "";
      /* Avançar os tokens até ao penúltimo */                                
      while (st.countTokens()>1) 
         st.nextToken();
      /* O último token é o último nome (apelido) da pessoa. */
      return st.nextToken();
   }
        
  /**
      Obter o nome completo.
   */
   public String getFullName()   { return name; }        

  /**
      Obter a data de nascimento.
   */
   public Calendar getBirthDay() {  return birthDay;   }

  /**
      Obter o endereço.
   */
   public String getAddress()     { return address;     }

  /**
      Obter os contactos.
   */
   public Contact getContact() { return contact; }

   // Métodos para alterar os atributos da pessoa.
   
  /**
      Alterar a data de nascimento.
   */
   public void setBirthDay( Calendar newBirthDay) {  birthDay = newBirthDay;  }

  /**
      Alterar a morada.
   */
   public void setAddress(String newAddress) { address = newAddress;  }

  /**
      Alterar os contactos.
   */
   public void setContact(Contact newContact) { contact = newContact; }        
      
  /**
      Construir uma String com os campos que descrevem a pessoa.
   */ 
   public String toString() {
      return getFirstName() +' '+ getLastName();
   }

  /**
      Comparar com outro objecto Pessoa.
      O critério de comparação é o primeiro nome.
      Tem que se garantir que o objecto passado como parametro é uma instancia da 
      classe Person. Caso contrário é um erro de programação.
   */
   public int compare( Comparable person )  {

      if( getFirstName().compareTo( ((Person)person).getFirstName() ) <0 )
         /* O nome do "this" é alfabeticamente inferior. */
         return CompareConstants.SMALLER;
      /* O nome do "this" é alfabeticamente superior ou igual. */
      return CompareConstants.BIGGER;
   }
        
  /**
      Verificar se duas pessoas são a mesma.
      A comparação tem como base os campos que definem univocamente a pessoa:
      - Nome.
      - Data de nascimento.
   */
   public boolean equals( Object object )  {
      if(object==null)
         /* O objecto "this" nao é "null". */
         return false;
               
      if( (object instanceof Person) == false ) 
         /* O objecto passado como parametro não é uma instância da classe Person. */   
         return false;   

      /* Efectuar a comparação dos objectos. */
      if(this==object) 
         /* "this" e "object" referem o mesmo objecto. */
         return true;
                
      /* Comparar apenas os atributos que definem univocamente uma pessoa. */                           
      if( compareNames(name,((Person)object).name) == true ) {
         if(birthDay.equals(((Person)object).birthDay)) 
            /* Trata-se da mesma pessoa. */  
            return true;
      }
      /* São pessoas diferentes. */                        
      return false;
   }

  /**
      Obter um objecto que implementa o critério de comparação por idade.
   */
   public static Compare getByAgeCriteria() {
      return new CompareByAge();
   }
}  /* Fim da classe Person */

