# JavaScript (Browser)

Whenever you want to start a Session from the frontend, you need to have a Template with
"Allow User Initiation" checked. Read more in the [Templates](https://www.wirequery.io/docs/features/templates) Feature
Chapter.

When you want the user to be able to record a session, you basically want to call WireQuery twice:

- Start the recording (and, more specifically, all queries in the template)
- Stop the recording

## Installation
Install the required libraries:
```bash
npm install rrweb rrweb-snapshot @wirequery/wirequery-js-core
```

## Recording
To start recording, create a RecorderClass and call `startRecoding`:
```ts
const wireQueryBackendPath = 'http://localhost:8080';

const url = '';
const templateId = '';
const apiKey = '';

const recorder = new RecorderClass(url, templateId, apiKey)

// start recording

recorder
    .startRecording({ accountId: '123' })
    .then(() => {
        // ...
    })

```
To end recording, call `recorder.stopRecording()`:
```js
// end recording
recorder.stopRecording()
    .then(() => {
        // ...
    })

```
The API Key is the API Key belonging to the specified template.
