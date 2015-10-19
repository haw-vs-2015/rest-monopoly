/player
  get:
    description: Liefert alle spieler Informationen
      responses:
        200:
          body:
            application/json:
              schema: | 
                {
                  "type": "object",
                  "description": "All players",
                  "properties": {
                    "players":
                      [ 
                        { 
                          "id": { "type": "int" },
                          "name": { "type": "string" } 
                        } 
                      ]
                  }
                }
              example: 
                '{ 
                    "players": 
                      [
                        {
                          "id": 1,
                          "name": "Mutermann1"
                        },
                        {
                          "id": 2,
                          "name": "Mutermann2"
                        }
                      ]
                }'
            
  post:
  /{playerName}
    description: Liefert eine neue spielerID und weist dieser den playerName zu.
      responses:
        200:
          body:
            application/json:
              schema: | 
                {
                  "type": "object",
                  "description": "A player",
                  "properties": {
                    "id": { "type": "int" },
                    "name": { "type": "string" }
                  }
                }
              example: 
                '{
                   "id": { 1 },
                   "name": { "Mutermann1" }
                }'
      
