Currently, there is no SDK available for the frontend. Nevertheless, it
is still easy to start a new screen capture

# Frontend Integration

In order to start a frontend recording, you need to install
`rrweb` and `rrweb-snapshot`.

A recording can be started by:
```
    fetch("https://demo.wirequery.io/api/v1/recordings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: ...templateId...,
        args: {
          ...template args...
        },
        lookbackSecs: 0,
        timeoutSecs: 30,
      }),
    })
```
When the recording is finished, the following call needs to be made:
```
      fetch(`https://demo.wirequery.io/api/v1/recordings/${recording.id}/finish`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          secret: recording.secret,
          recording: JSON.stringify(events),
          context: {},
        }),
      });
```
Where `events` represent `rrweb` events.

An example of how `rrweb` can be used, can be found in the `sdk/js/examples/transactions`.
