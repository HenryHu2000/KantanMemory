# KantanMemory
A software that help memorizing Japanese vocabulary

## Outline

Naming of the software:
- "Kantan" is the romaji of 簡単(かんたん) in Japanese, meaning "simple"

Platforms:
- Java version
  - Windows
- Web-based version (future plan)

License: LGPLv3

### Planned features
- Modes:
  - Memory recalling mode  - user decide if he is familiar with the word
     - Display words in kana(仮名)
     - Gradually give hints such as kanji(漢字)
  - Matching mode  - match word with correct meaning (future plan)
  - Spelling mode (future plan)
- Automatically select words for memorizing according to forgetting curve
- User can choose "too easy" to skip a word
- Alert for word memorizing
- Change language of the interface
  - Chinese
  - English
  - Japanese
- Multiple wordlists provided in package (future plan)
- User's customized wordlist (future plan)
- Read out the word (future plan)
  - Japanese text-to-speech
- Sync learning data to the cloud (future plan)



## Program
### Resource
- Japanese default word list
  - CSV file

### Dependencies (external libraries)
- Junit
- YAML parser
  - SnakeYAML
- CSV parser
- Japanese kana-romaji converter

### Structures
#### Kernel structures
- Daily wordlist generator
  - Wordlist generated consists of two parts:
    - New studying part: A proportion of the words consists of new words added into the memorizing word list
      - Specific amount of words everyday
      - From the software's default wordlist in software's package
      - Choose the word lists based on the user's choice in multiple/custom word list (future plan)
    - Revision part: Other proportion consists of old words
      - Selected from learning data, according to forgetting curve algorithm
- Process manager
  - Individual word manager
    - Based on how familiar the user is with an individual word, decides:
      - Should the user memorize the word again later
      - The familiarity of that word
  - Unrecognized word list manager
    - Add the user's unrecognized words again into the daily memorizing word list

##### Data-related structures
- Data reader
  - Read default word list CSV file from the package
  - Read the user learning data of old words
- Data writer
  - Edit the config file
  - Store learning data in YAML format

#### User interface structures
- Command UI
- GUI
  - AVG game-like user interface (future plan)

### Data
Format: YAML format

#### User learning data
Includes:
- List of words that the user has learned
  - For each word in the list the following data are recorded:
    - Name / ID number of the word
    - The date of last time the user has encountered that word
    - The number of times the user has encountered that word
    - User's familiarity of the word

#### Custom learning resource
- Customized wordlist (future plan)
  - Customized word lists added by the user
  - Can be chosen for new words added into daily word list

### Configuration
Includes:
- User's name
- Number of new words everyday
- Language of interface the user uses


## Terminology
- Word list: a list consist of the writing forms, kana pronunciation forms and stress number of different words 
  - Daily/current word list: the list of words that the user need to memorize in a single day
  - Default wordlist: the wordlist packed with the software, the new words in the daily wordlist will be from this list if not otherwise configured
     - Multiple default wordlist: more than one wordlist packed with the software
     - Custom wordlist: wordlist created by the user himself
- (Memorizing) process: in a daily word memorizing process, user need to go over each words in the daily wordlist
- Familiar/known/recognized: user believes he know this word. If confirmed, the word will no longer appear later in the daily wordlist
