# Dota2CompendiumStats

Dota2CompendiumStats is used to grab compendium-like statistics for Dota tournaments

There are 4 parameters, steam WebAPI key, league id, number of results per category, minimum number of games for hero stats

Ex. A9DA9DB70DDB1AB4D703FF11D45C7273 4266 5 5 20160220

A9DA9DB70DDB1AB4D703FF11D45C7273 - steam WebAPI key (Get one from here https://steamcommunity.com/dev/apikey) 4266 - league id for a tournament, 4266 is the id for the Shanghai Major

5 - Number of results per category such as "Highest Kill Average". For this example, it will display the top 5 heroes with the highest Kill average

5 - Minimum number of games for a hero to be picked to be considered for the "highest average" hero stats. In this example, a hero must be picked at least 5 times to be considered for the "highest average" hero stats.

20160220 - Grabs all matches after the start date. The date format is YYYY/MM/DD
