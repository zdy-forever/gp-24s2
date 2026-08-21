# Key requirements for the App

## Part 1: Basic App

You must create a basic app that enables users to: <br>
(i) authenticate (log in), (ii) view information, and (iii) search for content within the app.

For this, you must implement all the features listed below. Each feature is identified by an identifier followed by its difficulty level (e.g., easy, medium, hard). For example, the authentication feature's ID is [LogIn], and this feature is considered an easy implementation. The level of difficulty of a feature is less relevant than the quality of its implementation but may be used for marking purposes (for example for marking moderation purposes). Note that you must use this identifier in your report when referring to a feature. See the FAQ section for examples of some of the features.

### List of Basic Features:

1. `[LogIn]` The app must support user login functionality. User sign-up is not required. (easy)
   Important: The original marker-account credentials have been redacted from this public repository.
2. `[DataFiles]` The app must use a data set (which you may create) where each entry represents a meaningful piece of information relevant to the app. The data set must be represented and stored in a structured format as taught in the course. It must contain at least 2,500 valid instances. (easy)
   - Important: You must include in your report the link to the data file(s) on your project's GitLab repository and/or the link to your Firebase repository. In the latter case, you must add comp21006442@gmail.com as an Editor in your Firebase repository and mention this in your report.
3. `[LoadShowData]` The app must load and display data instances from the data set. Data must be retrieved from either a local file (e.g., JSON, XML) or Firebase. (easy)
4. `[DataStream]` The app must simulate user interactions through data streams. These data streams must be used to feed the app so that when a user is logged in (or enters a specific activity), the data is loaded at regular time intervals and the app is updated automatically.  (medium)
5. `[Search]` The app must allow users to search for information. Based on the user's input, adhering to pre-defined grammar(s), a query processor must interpret the input and retrieve relevant information matching the user's query. The implementation of this functionality should align with the app’s theme. The application must incorporate a **tokenizer and parser** utilizing a formal grammar created specifically for this purpose. (medium)
6. `[UXUI]` The app must maintain a consistent design language throughout, including colors, fonts, and UI element styles, to provide a cohesive user experience. The app must also handle orientation changes (portrait to landscape and vice versa) gracefully, ensuring that the layout adjusts appropriately. (easy) 
7. `[UIFeedback]` The UI must provide clear and informative feedback for user actions, including error messages to guide users effectively. (easy)

The underlying implementation must also:
 1. make proper use of data structures such as arrays, maps, etc., and fully implement <u>at least one</u> tree data structure taught in this course (AVL, RBT, or B-Tree) to improve efficiency.
 2. implement <u>at least three</u> design patterns covered in the course. Note that this requirement pertains to the actual implementation of design patterns. The use of third-party code, such as `FirebaseDatabase.getInstance()`, does not count toward this requirement.
 3. be sufficiently tested for all unit-testable components using JUnit 4 (excluding non-UI and external services). Exceptions should be properly handled within the app and not exposed to the user.

## Part 2: Custom features of the App

In addition to the basic features, the software should be enhanced with features that 
provide useful functionalities and improve the user experience. You must implement 
exactly <u>five custom features</u> (in addition to the features implemented in "Part 1: Basic app"). These features must be integrated into your app in a meaningful way. We value quality over quantity. The diversity of the implemented features is also important. Note that the features listed are related to topics covered in the course, such as design patterns, data structures, parsers, etc. You should reflect on each feature to determine the best way to implement it using the knowledge acquired during the course. We do not explicitly detail how each feature is related to the course content; it is your responsibility to make these connections.

## List of Custom Features

### <span style="color:#4a86e8">Search-related features</span>

1. `[Search-Invalid]` The search functionality must not only provide results for valid search queries (extension of the basic <b>\[Search\]</b> feature) but also process and correctly handle partially invalid search queries, returning meaningful results. Refer to the [Feature Request Example](#feature-request-example) for further explanation of this feature. (medium)
2. `[Search-Filter]` The app must provide functionality to sort and filter a list of items returned from searches using appropriate UI components. (easy)
3. `[Search-Designate]` The app must rank search results based on the status of the users. For example, a user may have multiple roles within the app, which should result in different ranked lists of results. (medium)

### <span style="color:#4a86e8">UI Design and Testing</span>

1. `[UI-Layout]` The app must incorporate appropriate layout adjustments for UI components to support both portrait and landscape orientations, as well as various screen sizes. This requirement is in addition to the [UXUI] basic feature and necessitates the implementation of new layouts for each orientation and screen size. (easy)
2. `[UI-Test]` The app must include comprehensive UI tests using [Espresso](https://developer.android.com/training/testing/espresso) (not covered in lectures/labs), ensuring reasonable quality and coverage of the app. (hard)

### <span style="color:#4a86e8">Greater Data Usage, Handling and Sophistication</span>

1. `[Data-Formats]` The app must read data from local files in at least two different formats, such as JSON, XML, etc. (easy)
2. `[Data-Profile]` The app must include a Profile Page for users (or any relevant entity within your app’s theme) that displays a media file, such as an image, animation (e.g., GIF), or video. (easy)
3. `[Data-GPS]` The app must utilize GPS information based on location data. Hint: see the demo presented by our tutors on ECHO360. (easy)
4. `[Data-Graphical]` The app must include a graphical report viewer that displays a report with useful data from the app. No marks will be awarded if the report is not graphical. (hard)
5. `[Data-Deletion]` Implement deletion functionality for your chosen tree data structure (RB-Tree, AVL-Tree, or B-Tree). The deletion functionality must serve a specific purpose within your application (medium)

### <span style="color:#4a86e8">Creating Processes</span>

1. `[Process]` The app must implement a sequence of actions or steps (at least three) to follow up on a process relevant to the app’s theme.  (hard)
2. `[Process-Vis]` The app must include a graphical element to visualize the progress of a process. (medium)
3. `[Process-Interact]` The app must allow users to micro-interact with and/or add messages to each step of the process. (easy)
4. `[Process-Permission]` Only users with permission (e.g., admin users, users who created the process, etc.) may view messages associated with the processes. (easy)

### <span style="color:#4a86e8">Resource Management</span>

1. `[Transaction]` The app must support transferring resources from one account to another. Each user or organization must have the ability to transfer and receive resources from others based on the transactions. (medium)
2. `[Transaction-Log]` The app must log all transactions for transparency. Transactions may be reported in either textual or graphical format. (easy)
3. `[Transaction-Approval]` Transaction approval. Transactions may require approval from a user. Your app may implement an approval mechanism where a user receives a notification to confirm or approve the transaction after a booking or a transaction is made. If the transfer is rejected or not approved for a long time (e.g., not approved within 24 hours), then the resource must be transferred back. (hard)


### <span style="color:#4a86e8">Firebase Integration</span>

1. `[FB-Auth]` The app must use Firebase to implement user authentication and authorisation. (easy)
2. `[FB-Persist]` The app must use Firebase to persist all data utilised within the app. (easy)

*Note that the implementation of Firebase custom features will be considered as basic features if they replace them.*

### <span style="color:#4a86e8">Peer to Peer Messaging</span>

1. `[P2P-DM]` The app must provide users with the ability to send direct, private messages to each other. (hard)
2. `[P2P-Block]` The app must provide users with the ability to block other users and prevent them from sending direct messages. (medium)
3. `[P2P-Restriction]` The app must enable users to restrict who can message them based on specific associations (e.g., allowing messages only from users within the same location, relationship status, etc). (hard)

### <span style="color:#4a86e8">User Interactivity</span>

1. `[Interact-Micro]` The app must provide the ability to micro-interact with items or users (e.g., like, block, connect to another user) with interactions stored in-memory. (easy)
2. `[Interact-Follow]` The app must provide the ability to follow, save or collect items. There must be a section that displays all items followed, saved or collected by a user, with items grouped and ordered. This information should be stored in-memory. (hard)
3. `[Interact-Noti]` The app must provide the ability to send notifications. A notification should be sent only after a predetermined number of interactions have occurred (e.g., when two or more micro-interactions have occurred). However, the type of interaction may override the predetermined number, with urgent interaction types triggering immediate notifications. (medium)
4. `[Interact-Scheduled]` The app must support scheduled actions, allowing users to schedule at least two different types of actions (e.g., send a report at 5 PM every day). (medium)

### <span style="color:#4a86e8">Privacy</span>

1. `[Privacy-Request]` The app must allow users to send requests to view certain content. These requests must be accepted or denied by the relevant user(s). (easy)
2. `[Privacy-Visibility]` The app must support at least two types of visibility (e.g., public, private). Users can only view profiles or content that are set to Public or those for which access has been granted after a request has been accepted. (easy)
3. `[Privacy-Block]` The app must provide content providers (or users) with the ability to block other users or specific contents/profiles. Once blocked, the user shall not be able to view the relevant contents in search results. (hard)

***A gentle reminder: A feature that does not work is not a feature, it is a bug.***

Note that an excellent implementation of both custom and basic features will be valued more than simply having functional features. Consider how each feature relates to the concepts taught in the course. We proposed each feature with these concepts in mind (e.g., you must think about how to properly design classes, effectively use data structures, appropriately incorporate design patterns, implement proper testing, etc).


## Voice your feature

Is there a feature you would like to implement that is not listed here? No worries, you can post your idea on our "Voice your Feature" thread on Ed. We will assess the proposed features and, if it is approved, confirm the **difficulty classification** and `FeatureId`. Please note:

Please note:
1. Any features which are approved in the forum can be pursued by any group.
2. We will only accept new features until Friday of Week 8.

A feature suggestion MUST contain:
- Post subject: feature name.
- A description of the feature.
- Details as to what the feature would entail. (e.g., an additional tokeniser, custom B-Tree, etc.) - Why is this feature relevant to the  course? (brief explanation, link to the course content).
- Suggested difficulty level: (easy, medium, hard) followed by a short justification.

Please try and keep any features mentioned relevant to the course
content and the core software design criteria. Any features that stray
too far from either of these will be refused.

### Feature Request Example
> - Subject: Partially valid and invalid search query handling.
> - Description: The application’s search bar will be able to handle both valid and invalid search queries without crashing the application and still provide the user with at least some search results in line with what was valid.
> - What the feature entails: Modifying the tokeniser/parser to analyse and process partially valid queries using advanced text similarity algorithms. This may include implementing techniques like Levenshtein distance, cosine similarity, or fuzzy matching to detect and correct minor errors or typos in the search query. The system should be able to compare user input against a database of valid tokens or keywords, make educated corrections or suggestions, and return relevant results based on the corrected query.
> - Feature relevance: Tokenisation and parsing.
> - Suggested Difficulty Level: Medium.



## Surprise!

Building software can be an exciting activity. Requirements may change or be added, and you may need to adapt your project to meet them. This is how it works in REAL LIFE, and we simulate it here.

Be prepared for changes! Build your software in a way that is *easy to extend and change*.

At some point in Week 10, you will be asked to create the new
feature and adapt your software to it. This is to practice the
software development/construction process.
