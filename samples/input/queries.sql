SELECT * FROM Sailors;
SELECT Sailors.A FROM Sailors;
SELECT Boats.F, Boats.D FROM Boats;
SELECT Reserves.G, Reserves.H FROM Reserves;
SELECT * FROM Sailors WHERE Sailors.B >= Sailors.C;
SELECT Sailors.A FROM Sailors WHERE Sailors.B >= Sailors.C
SELECT Sailors.A FROM Sailors WHERE Sailors.B >= Sailors.C AND Sailors.B < Sailors.C;
SELECT * FROM Reserves, Boats WHERE Reserves.G = 4;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 1000 ORDER BY Boats.E,Reserves.H;
SELECT Sailors.C, Reserves.H FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 150;
SELECT * FROM TestTable3;
SELECT * FROM TestTable3, Boats WHERE TestTable3.N < Boats.D;
SELECT * FROM TestTable3, TestTable4;
SELECT * FROM Sailors S;
SELECT * FROM Sailors S WHERE S.A < 3;
SELECT S.A FROM Sailors S;
SELECT DISTINCT S.A, R.G FROM Sailors S, Reserves R, Boats B WHERE S.B = R.G AND S.A = B.D AND R.H <> B.D AND R.H < 100 ORDER BY S.A;
SELECT S.A, R.G FROM Sailors S, Reserves R, Boats B WHERE S.B = R.G AND S.A = B.D AND R.H < 50 AND R.H = B.D;

