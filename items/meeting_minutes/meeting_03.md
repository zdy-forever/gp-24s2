# [yozusoft]

## Team Meeting [3] - Week [9] - [10-5] (21:15-22:00)
<br>
**Lead/scribe: Lanping Hu**

## Agreed Procedure
Stand up Procedure: 
- {e.g. Each team member briefly shares their progress and any roadblocks.}


## Agenda Items
| Number   |        Item |
|:---------|------------:|
| [1] | Communicate about our respective current progress |
| [2] | Briefly introduce the code structure we wrote |
| [3] | Discuss how to implement the remaining features |

## Meeting Minutes
- [70]

### Report - Summary of progresses
- Daoyan Zhu worked on search, tree structure, friend request and bug fixes
- Lanping Hu worked on notice, setting, booking timeline and bug fixes
- Jiahe Qian worked on OSM map and pushpin
- Jiahe Qian worked on some tests
- Shangyi Shen worked on p2p message and bug fixes

### Discussions
- How to transfer the locally stored gym list to booking
- How to notify new p2p messages
- How to implement another 2 design patterns

### Summary of decisions
- Change all comments into English
- Use a list to store gyms locally, pass in a string, no need to search
- Monitor the corresponding database changes to notify p2p
- Adopt Observer design pattern for notifying users who book the same place

### Matters to be confirmed
- Things to do before checkpoint2
- Demo video and follow-up schedule
- The remaining design pattern

## Action Items
| Task                                   | Assigned To |  Due Date  |
|:---------------------------------------|:-----------:|:----------:|
|        finish gym location list                        |  Jiahe Qian   | 10.7 |
|      add gym location into booking                 |  Lanping Hu          | 10.7 |
|      finish p2p message notification              |  Lanping Hu          | 10.6 |
|      finish the rest of booking management           |  Hanjian Jin        | 10.13 |
|            finish p2p message             |    Shangyi Shen      | 10.7 |
|            2 design patterns           |    TBD     | 10.13 |


## Scribe Rotation
The following dictates who will scribe in this and the next meeting.
| Name |
| :---: |
| [Lanping Hu] |
| [Daoyan Zhu] |
| [Shangyi Shen] |
| [Jiahe Qian] |
| [*Hanjian Jin] |