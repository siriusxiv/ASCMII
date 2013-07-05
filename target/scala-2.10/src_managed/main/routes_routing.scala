// @SOURCE:E:/git/ASCMII/conf/routes
// @HASH:c8335a091e8ec455be8d06b9ae41b69eae3ff8e6
// @DATE:Fri Jul 05 09:08:59 CEST 2013


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:6
private[this] lazy val controllers_Application_index0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:9
private[this] lazy val controllers_Assets_at1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
        

// @LINE:13
private[this] lazy val controllers_Application_profLogin2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("prof/login"))))
        

// @LINE:15
private[this] lazy val controllers_Application_profAuthenticate3 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("prof/login"))))
        

// @LINE:17
private[this] lazy val controllers_Application_profSeancesListe4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("prof/seances"))))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """prof/login""","""controllers.Application.profLogin()"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """prof/login""","""controllers.Application.profAuthenticate()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """prof/seances""","""controllers.Application.profSeancesListe()""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Application_index0(params) => {
   call { 
        invokeHandler(controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Nil,"GET", """ Home page""", Routes.prefix + """"""))
   }
}
        

// @LINE:9
case controllers_Assets_at1(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        

// @LINE:13
case controllers_Application_profLogin2(params) => {
   call { 
        invokeHandler(controllers.Application.profLogin(), HandlerDef(this, "controllers.Application", "profLogin", Nil,"GET", """prof login""", Routes.prefix + """prof/login"""))
   }
}
        

// @LINE:15
case controllers_Application_profAuthenticate3(params) => {
   call { 
        invokeHandler(controllers.Application.profAuthenticate(), HandlerDef(this, "controllers.Application", "profAuthenticate", Nil,"POST", """""", Routes.prefix + """prof/login"""))
   }
}
        

// @LINE:17
case controllers_Application_profSeancesListe4(params) => {
   call { 
        invokeHandler(controllers.Application.profSeancesListe(), HandlerDef(this, "controllers.Application", "profSeancesListe", Nil,"GET", """""", Routes.prefix + """prof/seances"""))
   }
}
        
}
    
}
        