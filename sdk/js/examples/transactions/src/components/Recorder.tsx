import { useState } from "react";
import { record, getRecordConsolePlugin } from "rrweb";
import { useSWRConfig } from "swr";

const events: any = [];

export const Recorder = () => {
  const { mutate } = useSWRConfig()

  const clearCache = () => mutate(() => true, undefined, { revalidate: true })

  const [recording, setRecording] = useState<any | undefined>([]);

  const start = () => {
    console.log("effect");
    const destructor = startRecording();
    return () => {
      console.log("unmount");
      destructor?.();
    };
  };

  const startRecording = () => {
    fetch("https://demo.wirequery.io/api/v1/recordings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: 3, // Debug Session by Account Id
        args: {
          accountId: "NL69FAKE8085990849",
        },
        lookbackSecs: 0,
        timeoutSecs: 30,
      }),
    }).then((res) => res.json().then((json) => {
      setRecording(json);
      clearCache(); // Trigger all calls once more
    }));
    return record({
      emit: (event) => {
        console.log(event);
        events.push(event);
      },
      plugins: [getRecordConsolePlugin()],
    });
  };

  const send = () => {
    if (recording) {
      console.log(events);
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
    }
  };

  return (
    <div>
      <button onClick={start}>Start</button>
      <button onClick={send}>Send</button>
      <button onClick={() => console.log('log test')}>Log</button>
    </div>
  );
};
