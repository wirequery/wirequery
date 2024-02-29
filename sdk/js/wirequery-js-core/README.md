# WireQuery JS Core

WireQuery JS Core allows you to start frontend recordings for use in Session Replay sessions in WireQuery.

Installation:
```bash
npm i @wirequery/wirequery-js-core
```

Usage:
```js
// create a Recorder
const recorder = new Recorder(
    "<wirequery url>",
    <template id>,
    "<api key>"
);

// start recording
recorder.startRecording({ args });

// stop recording
recorder.stopRecording();
```
