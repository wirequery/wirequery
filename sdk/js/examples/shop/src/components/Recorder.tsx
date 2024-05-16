// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import {Affix, rem} from "@mantine/core";
import {useState} from "react";
import {Recorder as RecorderClass} from "@wirequery/wirequery-js-core";

const wireQueryBackendPath = 'http://localhost:8080';

const recorder = new RecorderClass(wireQueryBackendPath, 1, '1/961a08c1-9c4d-44e9-acab-956cd337f6a7')

export const Recorder = () => {
    const [recording, setRecording] = useState<any | undefined>(undefined)

    const start = () => {
        recorder
            .startRecording({ accountId: '123' })
            .then(() => {
                setRecording(true) })
    };

    const send = () => {
        if (recording) {
            recorder.stopRecording()
                .then(() => {
                    setRecording(false)
                })
        }
    };

    return (
        <Affix position={{bottom: 0, right: rem(20)}}>
            {recording
                ? <button onClick={send}>Done</button>
                : <button onClick={start}>Start Recording</button>}
        </Affix>
    );
};
