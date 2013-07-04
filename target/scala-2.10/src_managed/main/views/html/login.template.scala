
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object login extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(authenticate: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.24*/("""
<html>
 <head>
  <title>ASCMII - Authentification</title>
  <link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*5.53*/routes/*5.59*/.Assets.at("images/options.png"))),format.raw/*5.91*/("""">
  <link rel="icon" type="image/png" href=""""),_display_(Seq[Any](/*6.44*/routes/*6.50*/.Assets.at("images/options.png"))),format.raw/*6.82*/("""">
  <link rel="stylesheet" type="text/css" media="screen" href=""""),_display_(Seq[Any](/*7.64*/routes/*7.70*/.Assets.at("stylesheets/login.css"))),format.raw/*7.105*/("""">
 </head>
 <body>
 	<h1>ASCMII</h1>
 	"""),_display_(Seq[Any](/*11.4*/if(authenticate=="F")/*11.25*/{_display_(Seq[Any](format.raw/*11.26*/("""
 		<p>Mauvaise combinaison d'identifiant et mot de passe.</p>
 	""")))})),format.raw/*13.4*/("""
 	<form action="/prof/login" method="POST">
 		<h2>Identification</h2>
 		<p>
 			<input type="login" name="login" placeholder="Identifiant">
 		</p>
 		<p>
 			<input type="password" name="passw" placeholder="Mot de passe">
 		</p>
 		<p>
 			<button type="submit">OK</button>
 		</p>
 	</form>
 </body>
</html>"""))}
    }
    
    def render(authenticate:String): play.api.templates.Html = apply(authenticate)
    
    def f:((String) => play.api.templates.Html) = (authenticate) => apply(authenticate)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Thu Jul 04 14:18:55 CEST 2013
                    SOURCE: E:/ASCMII/app/views/login.scala.html
                    HASH: c7ea73fedcbaeea3727ebf54765a96d67656420c
                    MATRIX: 723->1|822->23|972->138|986->144|1039->176|1121->223|1135->229|1188->261|1290->328|1304->334|1361->369|1441->414|1471->435|1510->436|1609->504
                    LINES: 26->1|29->1|33->5|33->5|33->5|34->6|34->6|34->6|35->7|35->7|35->7|39->11|39->11|39->11|41->13
                    -- GENERATED --
                */
            