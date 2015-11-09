# rest-monopoly #

rest - http://scalatra.org/

## Build & Run ##

Live Debug (set custom port in build.sbt "port in container.Configuration := 4567")

    sbt ~container:start

Build stand alone

    sbt assembly



[http://localhost:4567/](http://localhost:4567/)

# Fragen #
1. Joint der Spieler automatisch wenn er ein Game erzeugt?
2. Was soll das mit dem Services? Seite geht nicht auf...
3. Wie Spieler Timeout erkennen.
4. Die id des spielers sollte fortlaufend sein, da andere Games des selben Spielernamen haben könnten?
5. Wozu id lowerCase und name uppercase?
6. Wird die uri des Spielers mitgegeben damit er erreichbar ist? Also anstatt localhost fügt der Spieler seine IP hinzu?.

# Quellen #

[Responce code objects](https://github.com/scalatra/scalatra/blob/develop/core/src/main/scala/org/scalatra/ActionResult.scala)

[Parameters example](http://www.scalatra.org/2.4/guides/http/actions.html)

[Random Parameters stuff](http://www.scalatra.org/2.4/guides/http/routes.html)



