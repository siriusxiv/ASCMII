// @SOURCE:E:/ASCMII/conf/routes
// @HASH:c8335a091e8ec455be8d06b9ae41b69eae3ff8e6
// @DATE:Thu Jul 04 21:16:04 CEST 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._
import java.net.URLEncoder

import play.api.mvc._
import play.libs.F

import Router.queryString


// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:9
// @LINE:6
package controllers {

// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:6
class ReverseApplication {
    

// @LINE:17
def profSeancesListe(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "prof/seances")
}
                                                

// @LINE:15
def profAuthenticate(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "prof/login")
}
                                                

// @LINE:13
def profLogin(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "prof/login")
}
                                                

// @LINE:6
def index(): Call = {
   Call("GET", _prefix)
}
                                                
    
}
                          

// @LINE:9
class ReverseAssets {
    

// @LINE:9
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          
}
                  


// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:9
// @LINE:6
package controllers.javascript {

// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:6
class ReverseApplication {
    

// @LINE:17
def profSeancesListe : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.profSeancesListe",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "prof/seances"})
      }
   """
)
                        

// @LINE:15
def profAuthenticate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.profAuthenticate",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "prof/login"})
      }
   """
)
                        

// @LINE:13
def profLogin : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.profLogin",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "prof/login"})
      }
   """
)
                        

// @LINE:6
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        
    
}
              

// @LINE:9
class ReverseAssets {
    

// @LINE:9
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        
    
}
              
}
        


// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:9
// @LINE:6
package controllers.ref {

// @LINE:17
// @LINE:15
// @LINE:13
// @LINE:6
class ReverseApplication {
    

// @LINE:17
def profSeancesListe(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.profSeancesListe(), HandlerDef(this, "controllers.Application", "profSeancesListe", Seq(), "GET", """""", _prefix + """prof/seances""")
)
                      

// @LINE:15
def profAuthenticate(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.profAuthenticate(), HandlerDef(this, "controllers.Application", "profAuthenticate", Seq(), "POST", """""", _prefix + """prof/login""")
)
                      

// @LINE:13
def profLogin(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.profLogin(), HandlerDef(this, "controllers.Application", "profLogin", Seq(), "GET", """prof login""", _prefix + """prof/login""")
)
                      

// @LINE:6
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq(), "GET", """ Home page""", _prefix + """""")
)
                      
    
}
                          

// @LINE:9
class ReverseAssets {
    

// @LINE:9
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          
}
                  
      