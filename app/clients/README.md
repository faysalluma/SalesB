# Client configurations

Each `*.json` file in this directory defines one client and all of its Android
product flavors. The file name must match its `id`.

All feature flags are required so that adding a client never silently enables or
disables functionality.

## Client with private and Store flavors

```json
{
  "id": "example",
  "displayName": "Example",
  "backendId": "example",
  "crashReportingEnabled": true,
  "features": {
    "billing": false,
    "subscriptionScreen": false,
    "exports": true,
    "receiptPrinting": true,
    "quickSignup": false,
    "productLimit": false,
    "saleLimit": false,
    "clientManagement": true,
    "rayonManagement": true
  },
  "flavors": [
    {
      "name": "example",
      "applicationId": "com.groupec.salesb.example",
      "businessBuild": true,
      "storeBuild": false
    },
    {
      "name": "exampleStore",
      "applicationId": "com.groupec.salesb.example.store",
      "businessBuild": true,
      "storeBuild": true,
      "sourceSet": "example"
    }
  ]
}
```

`backendId` is the stable identifier used to build client-specific URLs:
`public/<backendId>/` for the API and
`includes/config/<backendId>/uploads/` for uploaded files.

The server root is common to all clients. Its debug and release values are
defined by the `SERVER_URL` fields in `app/build.gradle.kts`.

The optional `sourceSet` property makes a flavor reuse resources and code from
`app/src/<sourceSet>/`. It is useful when the private and Store variants of a
client share the same customization.

These files are read by Gradle and are not packaged in the application.
