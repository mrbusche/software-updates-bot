java -jar build/libs/software-updates-bot-0.0.1-SNAPSHOT.jar
git add report/report.json report/report.md report/report-by-date.md report/report.rss.xml report/report.atom.xml
git add report/status.md report/status.rss.xml report/status.atom.xml
git commit report/report.json report/report.md report/report-by-date.md report/report.rss.xml report/report.atom.xml report/status.md report/status.rss.xml report/status.atom.xml -m"autocommit by bot"
git push
