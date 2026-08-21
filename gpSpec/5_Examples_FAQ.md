## Examples and Frequently Asked Questions (FAQs)

### <span style="color:#4a86e8">What kind of app should we develop?</span>

The key is to identify a problem that resonates with your community and create a solution that is both innovative and practical based on the main project theme. Focus on everyday challenges faced by people and how technology can provide practical solutions.

**Potential Ideas**:
- **Transit Recommendations**: Create an app to offer transport suggestions based on real-time traffic conditions.
- **Air Quality Tracking**: Create an app to monitor and report local air quality.
- **Public Infrastructure**: Develop an app that allows citizens to report issues and contribute to the improvement of the city.
- **Visitor Assistance**: Develop an app to help tourists navigate and enjoy the city with ease.
- **Smart Waste Management**: Develop an app to help residents report full or malfunctioning trash bins and optimize waste collection routes.
- **Energy Consumption Monitoring**: Create an app that allows users to monitor and manage their home or office energy consumption.
- **Emergency Alerts and Response**: Create an app that provides real-time alerts for emergencies (e.g., natural disasters, accidents) and allows users to report incidents and request help.
- **Smart Water Management**: Develop an app to monitor and manage water usage and quality in the city.


### <span style="color:#4a86e8">How should I develop the search mechanism?</span>

Your search mechanism is responsible for tokenizing the query, parsing
and evaluating it. Define your own grammar and document it. You can use
the [<u>CFG Stanford tool</u>](https://web.stanford.edu/class/archive/cs/cs103/cs103.1156/tools/cfg/)
to help you create your grammar.

For example, if the topic is to develop an educational app, you may opt
to implement search for students to browse all the courses available.

> Example query: java and #advanced and @2024-2
>
> In this case (with a corresponding grammar and parser implementation),
> the user is looking for courses with “java” in their **name** or **description**
> field, annotated with the **tag** “advanced”, and is offered is S2, 2024.
>
> Hint: Which data structure would be most appropriate (or efficient)
> for this case? Your group must decide how to implement it for
> efficiency, generalisability, extensibility, etc.

### <span style="color:#4a86e8">What kind of data should my app contain? What is a data instance?</span>

The data for your app must be stored in a file and structured in a way that is easy to retrieve and process.

For example, if your app’s data is stored in a local XML file, it might look like this:

```
... 
<courses> 
    <course code="2100"> 
        <department>COMP</department> 
        <semester start="07" end="12">2024</semester> 
        <name> ... </name> 
        <description> ... </description> 
    </course> 
    ... 
</courses> 
```

Note that the file is structured (i.e., has a schema to represent the information) representing data related to courses in an educational institution. Keep in mind that this may not be the best file format or structure for your app; you should determine the most appropriate format and structure for your needs. This choice will be evaluated.

A data instance is a meaningful piece of information in your app that represents an entity (e.g., person, course). In the XML example above, each "course" is an instance, containing information such as department, semester, name, and description. Each course entry represents a single instance of data.

*Note that you need to generate the data instances for your application as well as the data format/structure. You are free to choose any file format (JSON, XML, Bespoken, etc.). You can create a script to generate your data, a script to scrape from the Web or manually create it. You can also get data provided by some API and platforms (e.g., [Khan
Academy](https://www.khanacademy.org/computer-programming/wip-api-explorer/6616827407400960) API, [Kaggle](https://www.kaggle.com/datasets). This and only this script can be written in any programming language (Java, Python, etc). Please, make sure you are allowed to download the data from external sources and that the script is included in your repository.

**As an option, you can use Firebase (and JSON) if you feel comfortable with it and if this is the best choice for your app.

### <span style="color:#4a86e8">What about the data structures?</span>

You must know where, which, how and when to use it. It depends on how
you design your app. Think about what data structure you would use if
you needed to search for something. For example, if you go for a tree,
what would be the key of your tree? Is this the most appropriate data
structure for your app? Discuss with your group.

> Going back to our example: many students would search for courses
> offered in the next semester, by their department and at their desired
> level of studies. Which data structure would be most appropriate or
> efficient for this case? What is the key(s) of my tree(s)? Should I
> use one or multiple trees? Should I allow duplicate keys in the tree?
> Your group must make these design decisions.

For trees, please use the ones (RB, AVL, B-Tree) taught in the course unless otherwise approved in writing.

We will evaluate your choice, usage, and your justification (not only trees, but also other data structures such as arrays, lists, maps, etc).

### <span style="color:#4a86e8">Why and how to simulate a data stream?</span>

Simulating a data stream can simplify the development of your app by eliminating the need for a client-server model (which is not taught in this course). You can then create a realistic user experience by simulating interactions through a data stream.

To implement this, create data files that represent user actions within the app. For example, if you are developing an educational app like Wattle, your data files might include actions such as a lecturer posting an announcement or a student asking/answering a question on the forum. When you start the app, these actions will appear in the UI, such as new questions/answers "popping up" or notifications for new announcements.

To achieve this, you can develop a module to read from these data files at regular intervals (e.g., every few seconds) and update your app with the new information.

Note that there is a Wiki article written by a former student that may be helpful for this task. (You should also consider writing a good article to share your knowledge with your colleagues).

### <span style="color:#4a86e8">What does `[stored in-memory]` mean?</span>

It means that for the feature, storing data in the temporary memory
(until the app is closed) would be enough. Meanwhile, storing data
persistently on the device or remote databases is also acceptable (and
may in fact be more appropriate if the data is meant to be persisted).

### <span style="color:#4a86e8">Why and how to implement these features?</span>

Each feature is designed considering one or more concepts covered in the
course. Think about what design pattern, data structure, persistence
method, … you can use to implement it.

On the other hand, your application is only a prototype that showcases
how your idea could be developed and implemented in a real application.
Please prioritise your effort on the <u>core aspects of</u> <u>this
project</u> before developing additional features. 

### <span style="color:#4a86e8">How can I incorporate the required features in the app?</span>

Start by choosing features that possibly fit into your App, and then align them with your problem statement. Consider how design patterns, data structures, and persistence methods can be utilized to implement these features in a way that enhances the app's functionality.

The following are some examples of how the features can be designed to meet the requirements of different features.

> [Process] The app must implement a sequence of actions or steps (at least three) to follow up on a process relevant to the app’s theme.  (hard)

For example, in a Smart Parking app, you might have (step 1) users select a car park and check its availability, (step 2) book a slot with their desired times (e.g. 3 pm - 4 pm at City West Car Park. Fill in your personal information and car plate number. The booking can be then reviewed on the Booking page), (step 3) At 3pm, receive a notification to check in. The user needs to click the check-in button on the Booking page to generate the parking ticket, which is shown on the Current Ticket page. (step 4) At 4pm, the user leaves the car park with his car. The app then generates a receipt for the user which can be reviewed on the Past Parking page.

> [Process-Vis] The app must include a graphical element to visualize the progress of a process. (medium)

For example (Smart parking app again), you might show the remaining time for a parking slot or track the location of a bus or parcel in real-time.

> [Transaction] The app must support transferring resources from one account to another. Each user or organization must have the ability to transfer and receive resources from others based on the transactions. (medium)

If your app supports transactions, such as paying utility fees or donating resources, ensure that users can easily transfer resources between accounts.

> [Transaction-Approval] Transaction approval. Transactions may require approval from a user. Your app may implement an approval mechanism where a user receives a notification to confirm or approve the transaction after a booking or a transaction is made. If the transfer is rejected or not approved for a long time (e.g., not approved within 24 hours), then the resource must be transferred back. (hard)

Continued from the above example, you might also opt to implement a transaction approval mechanism where users receive notifications to confirm or approve a transaction. If a transaction is not approved within a specified time frame, the resources should be returned to the sender.

> [Interact-Follow] The app must provide the ability to follow, save or collect items. There must be a section that displays all items followed, saved or collected by a user, with items grouped and ordered. This information should be stored in-memory. (hard)

For example, users might follow second-hand goods for exchange or save preferred car parks or driving routes, with this information stored in-memory.

> [Interact-Noti] The app must provide the ability to send notifications. A notification should be sent only after a predetermined number of interactions have occurred (e.g., when two or more micro-interactions have occurred). However, the type of interaction may override the predetermined number, with urgent interaction types triggering immediate notifications. (medium)

For instance, this could be used for second-hand goods exchange requests or utility payment reminders.


### <span style="color:#4a86e8">Which design pattern should I use?</span>

You must know where, which, how and when to use it. It depends on how you design your app and the features you implement. Several features were proposed thinking about the most appropriate design pattern to be used. Remember also to explain and justify your implementation in detail.

### <span style="color:#4a86e8">How complicated should my grammar be?</span>

It does not need to be complicated, but it must be formal, unambiguous and easily extendable. Most importantly, you have to demonstrate that you know how to implement it and be consistent with your app theme.


### <span style="color:#4a86e8">Can I use the lab code?</span>

Yes, you can, but **but you must reference it in the Statement of Originality 
and the code and use the report to show us what you have improved in the lab's code**. 
There are several ways to improve the code from our labs. Remember that 
we will **only assess your code, not ours**. Only significant improvements will count toward your completion of a feature. 
Failure to report code/material reuse may result in an academic integrity investigation.

### <span style="color:#4a86e8">Can I use pre-made solutions (e.g. Firebase or APIs)?</span>

Yes, you can and should use pre-made solutions; reuse is a key part of software construction. However, this assignment includes some compulsory items (e.g., a tree data structure), and we expect you to implement them. If pre-made solutions do not affect the core features you need to implement, their use is acceptable.

Database Management Systems (DBMS) like MySQL, Firebase are generally unsuitable for this assignment because they can replace many of the features and data structures that are central to the assessment and that you are expected to implement. Additionally, there is a risk that if the DBMS is not configured or packaged correctly, it could result in a loss of marks because we might not be able to evaluate it properly. 

Focus on mastering the foundational concepts rather than relying solely on technology. Technology changes, but foundational principles remain constant.

