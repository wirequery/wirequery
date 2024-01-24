# JavaScript (Browser)

Whenever you want to start a Session from the frontend, you need to have a Template with
"Allow User Initiation" checked. Read more in the [Templates](https://wirequery.io/docs/features/templates) Feature Chapter.

When you want the user to be able to record a session, you basically want to call WireQuery twice:

- Start the recording (and, more specifically, all queries in the template)
- Stop the recording

In order to be able to start a frontend recording, you need to install `rrweb` and `rrweb-snapshot`.

Currently, there is no SDK available for the frontend. Nevertheless, it is very easy to integrate the frontend
with WireQuery using a few simple `fetch` commands and using `rrweb`.

## Start Recording

A recording can be started by calling the `recordings` endpoint from WireQuery.
```
const res = await fetch("<WireQuery Host>/api/v1/recordings", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify({
    templateId: <templateId>,
    args: {
      <template args>
    },
  }),
})

const recording = await res.json()

```
Here, the `<templateId>` has to be set to the Template id mentioned earlier. The args need to container
the parameters of that template, such as the `accountId`. For example:

```
const res = fetch("https://demo.wirequery.io/api/v1/recordings", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify({
    templateId: 1,
    args: {
      accountId
    },
  }),
})

const recording = await res.json()
```

## Recording

The actual recording needs to be taken care by `rrweb`. A guide on `rrweb` can be found on
[GitHub](https://github.com/rrweb-io/rrweb/blob/master/guide.md), and an example can be found
in the [Shop](https://github.com/wirequery/wirequery/tree/main/sdk/js/examples/shop) example.

## Finish Recording

Similarly, when a recording is finished, a call to WireQuery needs to be made as well to send and finalize the recording.

When the recording is finished, the following call needs to be made:
```
fetch(`<WireQuery Host>/api/v1/recordings/${recording.id}/finish`, {
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
Where `events` represent `rrweb` events and `<WireQuery Host>` is the host of WireQuery, such as `https://demo.wirequery.io`.

An example of how `rrweb` can be used, can be found in the `sdk/js/examples/shop` in the [WireQuery](https://github.com/wirequery/wirequery) repository.

## Examples

The following examples demonstrate how WireQuery can be used within a Frontend application:

- [Shop](https://github.com/wirequery/wirequery/tree/main/sdk/js/examples/shop) - simulates a webshop.
    - [Products Service](https://github.com/wirequery/wirequery/tree/main/sdk/jvm/examples/spring-boot/products) - simulates a product catalogue.
    - [Basket](https://github.com/wirequery/wirequery/tree/main/sdk/jvm/examples/spring-boot/basket) - simulates an order basket. Connects to the products service.
