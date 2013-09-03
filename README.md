semanticSolver
==============

Using Linked Data to solve crossword clues

Development timeline [Task - Target completion date]:

Choose a domain of knowledge - April 2013 - Pop music - done, 22nd July 2013

Familiarise myself with Apache Jena - April 2013 - done, 3rd August 2013

Extract a test dataset from the Web of Data that covers the chosen domain - April 2013 - done, 24th July 2013

Define a crossword-clue ontology that covers a small number of simple clue patterns and relationships covered in the test dataset - June 2013 - done, 8th August 2013

Choose a source of crossword clues covering my chosen domain to act as a test - June 2013 - done, 2nd August 2013. Three primary sources:
- Sit & Solve Pop Music Crosswords by Jeffrey Harris (2012), Sterling.
- The Everything Music Crosswords Book by Charles Timmerman (2007), Adams Media.
- Entertainment Crosswords: Movies, Music, Broadway, Sports, TV, & More! by Sam Bellotto Jr. (2012), Imagine Publishing.

Build a clue-parsing module that is able to match recognised words and phrases in a clue given as a plain text string to concepts present in the crossword clue ontology and output a set of matching RDF statements - June 2013 - done, 17th August 2013 

Build a prototype query module that takes the output of the clue-parsing module and retrieves from the locally stored test dataset any candidate RDF triples matching the query criteria - July 2013 - done, 17th August 2013

Build an answer-generating module that takes the output of the query module and builds a list of candidate solutions to the given clue - July 2013 - done, 17th August 2013

Develop a simple front-end for the system that enables a user to enter a clue as a text string and receive an answer to the clue in response - July 2013 - done, 14th August 2013

Extend the query module to retrieve data directly from DBpedia’s SPARQL endpoint - July 2013 - done, 15th August 2013

Define the language of which clues must be members in order for the application to successfully provide a solution, and identify any gaps in the test base that this language fails to cover - August 2013

Refine the crossword clue ontology to cover any identified gaps. Redefine the language that the revised ontology covers - August 2013

Complete first draft of project report and submit to supervisor for feedback - August 2013

Complete and submit project report - September 2013


Non-core objectives:

Develop a semantic relatedness algorithm optimised to allow the answer-generating module to rank its candidate solutions in a probability-weighted list - done, 26th August 2013

Develop a knowledge base of RDF statements that define the answers to previously encountered clues; this could potentially be used by future crossword-solving applications - done, 2nd September 2013