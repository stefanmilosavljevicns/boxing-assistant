# REST API

All routes are under `/api/v1`. JSON fields retain the current client contract. `prepareMin` is the external JSON name; MongoDB continues to use the historical stored field `preparetMin`.

## Workout JSON

```json
{
  "id": "optional-on-create",
  "name": "Intervals",
  "moves": ["Jab", "Cross"],
  "movesTime": [30000, 30000],
  "roundNumber": 3,
  "roundMin": 2,
  "roundSec": 30,
  "restMin": 1,
  "restSec": 0,
  "prepareMin": 0,
  "prepareSec": 10
}
```

`movesTime` values are milliseconds in the Android client. `name` must be nonblank, `moves` must be nonempty, `movesTime` must be present, `roundNumber` must be positive, minute values nonnegative, and second values from 0 to 59. The current validation does not check equal lengths of `moves` and `movesTime`, individual move names, or uniqueness of workout names.

## Endpoints

| Method and path | Behavior | Responses |
|---|---|---|
| `GET /getAllWorkouts` | Return all stored workouts | `200` with array |
| `GET /getAllNames` | Return distinct workout names | `200` with string array |
| `GET /getWorkout/{name}` | Find one workout by its name | `200` with workout, `404` if absent |
| `POST /addWorkout` | Create a workout | `201` with saved workout, `400` for invalid/malformed JSON |
| `PUT /updateWorkout/{name}` | Replace editable fields for the named workout; the existing Mongo ID is retained | `200` with saved workout, `404` if absent, `400` for invalid/malformed JSON |
| `DELETE /deleteWorkout/{name}` | Delete the workout found by name | `204` if deleted, `404` if absent |

Example create request:

```http
POST /api/v1/addWorkout
Content-Type: application/json
```

The body is the Workout JSON example above. The response is `201 Created` with the saved workout, including its Mongo-generated `id`.

Example lookup:

```http
GET /api/v1/getWorkout/Intervals
```

The route has no authentication or authorization. Names containing URL-reserved characters must be URL-encoded. Persistence failures currently use Spring's default server error handling. Duplicate names are not constrained, while name-based lookup/update/delete expects one match.
