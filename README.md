# rest-monopoly #

rest - http://scalatra.org/

## Dependencies ##

https://github.com/AlexHolly/util-scala-ws-http-client
https://github.com/AlexHolly/ip-manager

## Test, Run & Build ##

Test

    sbt test
    
Run

    sbt run
    
Live Debug (set custom port in build.sbt "port in container.Configuration := 4567")

    sbt ~container:start

Build stand alone

    sbt assembly

## Logging settings ##

    /resources/logback.xml -> root level


[http://localhost:4567/](http://localhost:4567/)

# Fragen #
1. Joint der Spieler automatisch wenn er ein Game erzeugt?
2. Wie Spieler Timeout erkennen.
3. Die id des spielers sollte fortlaufend sein, da andere Games des selben Spielernamen haben könnten?
4. Wozu id lowerCase und name uppercase?
5. Wird die uri des Spielers mitgegeben damit er erreichbar ist? Also anstatt localhost fügt der Spieler seine IP hinzu?.
6. Kann man die API in RAML durch nummerieren?
7. Tests schlagen manchmal fehl, warum? 
8. Service zu Service Kommunikation direkt oder per event service?
9. println zu log?

# Quellen #

[Responce code objects](https://github.com/scalatra/scalatra/blob/develop/core/src/main/scala/org/scalatra/ActionResult.scala)

[Parameters example](http://www.scalatra.org/2.4/guides/http/actions.html)

[Random Parameters stuff](http://www.scalatra.org/2.4/guides/http/routes.html)



