# ScrabbleProject

![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg)

Assignment Two of COMP90015

## Table of Contents 
- [Specification](#specification)
- [Project Management Rule](#project-management-rule)
 
    
## Specification
1. You need to create a 20x20 grid where users on different PCs, min of 2 people, but allowing more if there are more players, will place letters on tiles of this grid to make words in turns.
2. We ask you to implement a specific version of this game as follows but you are welcome to read about Scrabble game online which is a popular game with numerous versions.
3. Users will take turns to place a character in a tile of the fixed grid mentioned above.
4. When tiles that touch each-other make a word, then the person who placed the associated letter will get points equal to the total length (number of tiles) of the word.
5. A proper word is judged when all players accept the word through a visible voting GUI.
6. The game ends when all users have a turn and cannot find a word/extension to make, basically when all players say pass when their turn comes through a GUI.
7. Words are written in English and can only be read from left to right or top to bottom. 
8. The game should have appropriate GUI for allowing appropriate controls and information to play as well as to follow it by all players.
9. All users should be able to see the same view of the game on their machines without differences or errors between them. Start and end game should be clear.
10. Users should be able to logout from the game any time which would lead to end game as well.
11. There should be a game membership pool where users come in when they login to the game application and see other potential players. 
12. You are welcome to have simplifying assumptions for membership and game start management such as having at most one game at a time and not allowing people to join games at any time but only at the beginning of a game. 
13. You need to let any player initiate a game and invite others who can then accept this.
14. You can get creative and implement a more complex game if you agree as a team. 

## Project Management Rule
**参考自饿了么前端开发团队技术[文章](https://zhuanlan.zhihu.com/p/39148914)**

**[GitFlow](https://datasift.github.io/gitflow/IntroducingGitFlow.html)**
![alt](https://jeffkreeftmeijer.com/git-flow/git-flow.png)
### 名词定义
1. **fork:** 
2. **Brunch:**
3. **Pull Request:**
4. **upstream:**
5. **Code review:**
6. **Create a merge commit:**
7. **分支同步:** 将upstream上游develop分支的代码同步到本地和远端，以保证自己repo下代码为最新版本
8. **bugfix:** 为了修复master分支中的缺陷，创建的分支
9. **删除分支：** 一个分支审查通过并成功提交给上游后，可以被删除。

### 出现冲突的解决规则

### 结对编程
> Pair programming（结对编程）是一种敏捷开发方法，指的是两个程序员在一台计算机上共同工作。输入代码的人称作驾驶员，审查代码的称为观察员。
#### 主动情况

#### 被动情况
  test
### 项目开发流程图
