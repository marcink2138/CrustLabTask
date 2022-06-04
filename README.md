# CrustLabTask

Database -> SQLite
According to the [SQLite docs](https://www.sqlite.org/datatype3.html#date_and_time_datatype) dates are stored as an integer (milliseconds from 01.01.1970)

Inserting into users is triggering create_accounts trigger which is responsible for creating accounts (PLN,USD,EUR) for the newly created user.

location of db -> src/main/resources
