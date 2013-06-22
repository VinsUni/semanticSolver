semanticSolver
==============

Using Linked Data to solve crossword clues

Task                                                                            | Target completion date
----------------------------------------------------------------------------------------------------------
Choose a domain of knowledge. Needs to be reasonably small but                  | April 2013
needs to offer a reasonable potential for generating crossword clues.	        |
Pop culture – movies, or music, for example – is initially in mind      		|
----------------------------------------------------------------------------------------------------------
Familiarise myself with the ontology API, inference engine, and query engine of | April 2013
Apache Jena																		|
----------------------------------------------------------------------------------------------------------
Extract a test dataset from the Web of Data that covers the chosen domain		| April 2013
----------------------------------------------------------------------------------------------------------
Define a crossword-clue ontology that covers a small number of simple clue 		| June 2013
patterns and relationships covered in the test dataset							|
----------------------------------------------------------------------------------------------------------
Choose a source of crossword clues covering my chosen domain to act as a test 	| June 2013
base																			|		
----------------------------------------------------------------------------------------------------------
Build a clue-parsing module that is able to match recognised words and phrases 	| June 2013
in a clue given as a plain text string to concepts present in the crossword clue| 
ontology and output a set of matching RDF statements							|
----------------------------------------------------------------------------------------------------------
Build a prototype query module that takes the output of the clue-parsing module | July 2013
and retrieves from the locally stored test dataset any candidate RDF triples 	|
matching the query criteria														|
----------------------------------------------------------------------------------------------------------
Build an answer-generating module that takes the output of the query module and | July 2013
builds a list of candidate solutions to the given clue							|
----------------------------------------------------------------------------------------------------------
Develop a simple front-end for the system that enables a user to enter a clue as| July 2013
a text string and receive an answer to the clue in response						|
----------------------------------------------------------------------------------------------------------
Extend the query module to retrieve data directly from DBpedia’s SPARQL endpoint| July 2013
----------------------------------------------------------------------------------------------------------
Define the language of which clues must be members in order for the application | August 2013
to successfully provide a solution, and identify any gaps in the test base that |
this language fails to cover													|
----------------------------------------------------------------------------------------------------------
Refine the crossword clue ontology to cover any identified gaps. Redefine the 	| August 2013
language that the revised ontology covers										|
----------------------------------------------------------------------------------------------------------
Complete first draft of project report and submit to supervisor for feedback	| August 2013
----------------------------------------------------------------------------------------------------------
Complete and submit project report												| September 2013
----------------------------------------------------------------------------------------------------------