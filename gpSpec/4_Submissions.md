# Project Submission

Your group must fork the sample [CECC GitLab repository](https://gitlab.cecs.anu.edu.au/comp2100/group-project/gp-24s2) according to the instructions (private repository, do not change the slug), and share the correct link on Wattle before checkpoint 1.

For the codes and admin submissions, only the <u>final state</u> of your **main** 
branch will be assessed, and you must have all the report, minutes, APK, etc. 
files in your final commit (on Thu and Fri respectively). The project **cannot** 
be updated after the deadline (otherwise, you may get zero marks).

You must continuously update your work from the release of the assignment 
**until the submission deadline in Week 11**.

The sample repository provided some template files: **report.md** (template for your report); **checklist.md** (project minimum requirements); **statement-of-originality.yml**; **meeting-template.md** (a template for your minutes).


## <span style="color:#4a86e8">Code development</span>

- You **must** include `@author` annotations for **each java file** with the UID and name of the **key author**. If the author has significant contributions down to the level of methods or inner classes, etc., you should then provide annotations at the corresponding level (e.g., method docstring).
- Git commits:
  - You must commit **often (every week from Week 7)** and include **meaningful commit messages**.
  - When merging branches, do **NOT squash your commits**. Large commits are not accepted.
  - We expect to see **regular, meaningful commits** by each member during the **project's full span** from the commit history.
- Remove unused code files and code segments.

Use your own GitLab user account (UID) to commit your contributions. Add this information to your report. (Failing to do this may result in academic misconduct investigations).


## <span style="color:#4a86e8">Report (Important!)</span>
The report is an essential submission component that gives us **evidence and details** 
of your work and the outcomes. The **technical outcome** is a key aspect of the 
evaluation of this project, but your **reasoning process and teamwork** are just as important.

Therefore, you are required to produce a detailed report which justifies your work 
and outcomes. It must be in the form of a markdown file titled **report.md**, and 
you should start with the template which contains some description and examples of 
each item. This includes (but is not limited to):
- Administrative summary (must include information required to run your app. For example, link to your Firebase repository, API level used, etc).
- Summary of individual contributions.
- An app summary with screenshots.
- A list of examples/use cases of your app.
- A UML diagram (class diagram).
- Application Design and Decisions (include justification for decisions made, diagrams, etc.)
  - A summary of the basic App and implemented features
  - List all items/features implemented in your project (separate features into their categories using their ids).
- A summary of known errors/bugs (list of bugs).
- A testing summary section (including **number of test cases**, **description** of tests, 
  **coverage**, with <u>screenshots</u> and <u>links to the test files</u>, etc.)

**IMPORTANT**: For each section and item, you must provide a **description** of your 
design decision and justifications, as well as **links** to the relevant code segments, 
with line numbers highlighted in the links. When the same information applies to 
different parts, you must provide the links in ALL of the sections, not just one of them.

### <span style="color:#c95757">All attempted features MUST be documented</span>

> Note that if it is not included (with references and URLs) and explained in your report, we **will not consider** your features, data structures, design patterns, etc., as completed. It is your responsibility to report everything that was implemented in your project. We are not playing treasure hunt, and appeals will be rejected if what was implemented is not explicitly described with links (and line numbers if code) to the relevant files in your report.


## <span style="color:#4a86e8">Other important items</span>

Place all non-android documents into one folder named **Items** at the repository root. 
Make good use of links in your report to refer to these documents in the appropriate sections. These include:

- Team meeting minutes (<u>minimum 4)</u>
  - develop a clear **plan with work allocations** and document this in the 1st meeting’s minutes.
  - Commit the minutes and push to your repo right after every meeting (max commit date is 2 days after, otherwise they will not be considered).
- Your conflict resolution protocol - Define this protocol in your first meeting (mandatory).
- A statement of originality (all members must be listed and **each** should
  sign the document with their **own GitLab account** by <u>adding your own names,</u> committing and pushing your repository.
- You must bundle your app into a standalone APK that can be loaded and
  executed correctly on an AVD (see
  [<u>video)</u>](https://gitlab.cecs.anu.edu.au/comp2100/student-resources/comp2100-lab-videos/-/blob/main/gp-resources/GPAPKGenerationGuide.mp4?ref_type=heads).
- Create a video `features.mp4` that demonstrates each implemented feature (both Basic and Custom, in the same order as listed in the report), max 4 minutes (and 0 seconds) and 120MB. The video should showcase how your feature works in the running app with a clear voiceover narration that explains the feature being demonstrated (preferably with subtitles showing the FeatureID).


## <span style="color:#4a86e8">Individual Reflection and Peer- and Self-evaluation</span>

Each student must submit an individual reflection via Wattle, with the same due date as the group report in Week 11. This includes:

- **Individual reflection**: write 100-120 words related to your experience during the group project (be concise and direct). For example: how was your experience working in a team? Reflections on what your team could have done better, what worked and what did not work? How was the work divided and was that fair?
- **Peer and self-evaluation**: an evaluation of each member of your group:
  - Name - UID
    - Contribution: 25%
    - Justification: This member was responsible for the classes …, collaborated with others, wrote sections X and Y in the report.
  - Name - UID
    - Contribution: 10%
    - Justification: This member was unavailable most of the time within week 7-9, did not deliver some of the modules promised and recorded in the minutes (meetings 1-2). However, they completed some modules (…) and handled the minute madness ...

*"Contribution" measures the percentage contribution of each member. The **total contribution** of all members must <u>add to</u> 100%. Please include the <u>correct UID</u> of your teammates.

## Group Presentation (Minute-Madness)

There will be a group presentation during the **lecture in Week 12**. This is an opportunity to showcase your project and get feedback from your peers. The presentation will follow the Minute Madness format where each group will present a set of timed slides within a predetermined time (slides will automatically change every 30 seconds). The total number of groups will determine the duration of each presentation and the number of slides to fit within our lecture time. For your reference, the typical duration is between 1.5 and 2 minutes per group.

Your presentation must clearly present your topic and convince the audience that your app is innovative and could be used in the real world. You must briefly talk about the structure of your project, decisions made, and solutions for the problems faced during the project (use of a particular data structure, design pattern, etc.).

Your presentation must be clear, convey your ideas and give an overview of the software construction process you and your team experienced. I would recommend using some **screenshots** to show what your app looks like (images can be better than words sometimes).

More details related to the project demonstration will be released in Week 10. Slides must be uploaded to the then-announced link by the deadline **(Friday, Week 11)**. 
