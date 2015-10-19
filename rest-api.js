/dice:
  get:
    description: Gives you a single dice roll
    responses:
      200:
        body:
          application/json:
            schema: | 
              { "type": "object",
                "properties": {   
                  "number": {"type": "int"},
                  "required": true
                }, 
              }
            example: '{ "number": 4 }'

/games:
  get:
    description: Meta information about running games of restopoly
    responses:
      200:
        body:
          application/json:
            schema: | 
              { "type": "object",
                "properties": {
                  "id": {"type": "int"},
                  "players": list_of_players,
                  "round": {"type": "int"},
                },
                "required": [ "id" ]
              }

/boards/{gameid}:
  get:
    description: Information about the boards and locations
    responses:
      200:
        body:
          application/json:
            schema: | 
              { "type": "object",
                "properties": {
                  "id": {"type": "int"},
                  "fields": list_of_fields,
                },
                "required": [ "id" ]
              }
  /{playerId}:
    get:
      responses:
        200:
          body:
            application/json:
              schema: |
                { "type": "object",
                  "properties": {
                    "id": {"type": "int"},
                    "position": {"type": "int"},
                  }
                }

/banks/{gameid}:
  get:
    description: Money management
    responses:
      200:
        body:
          application/json:
            schema |
              { "type": "object",
                "properties": {
                  "balance": {"type": "number"},
                }
              }

/decks/{gameid}:
  /chance:
    get: 
      description: Gets a chance card
      responses:
        200:
          body:
            application/json:
              schema: card
              example: |
                { "name": "Go to Jail",
                  "text": "Go to jail, do not travel across 'go' and don't receive $200 "
                }
  /community
    get: 
      description: Gets a community chest card
      responses:
        200:
          body:
            application/json:
              schema: card
              example: |
                { "name": "Go to Jail",
                  "text": "Go to jail, do not travel across 'go' and don't receive $200 "
                }

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
      
