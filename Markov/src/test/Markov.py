import numpy as np
import numpy.linalg as alg
import scipy.linalg as spla
import sys


def LireMAt(taille):
    a = np.zeros((taille,taille))
    for i in range(taille):
        s = 0
        for j in range(taille):
            a[i][j]=(float(input("give the ["+str(i)+"]["+str(j)+"] value")))
            while((a[i][j] > 1) or (a[i][j] < 0)):
                a[i][j]=float(input("the value is rang , try an other one >0 or <1"))
            s += a[i][j]
        if s != 1:
            print("the value is rang")
            return None
    return a




def LireCMTD():
    taille = int(sys.argv[2])
    a = np.zeros((taille,taille))
    for i in range(taille):
        s = 0
        for j in range(taille):
            a[i][j]=float(sys.argv[j+taille*i+3])
            if ((a[i][j] > 1) or (a[i][j] < 0)):
                print("the value from ",i,"  to  ",j," is not valide")
                return None
            s += a[i][j]
        if s != 1:
            print("the ligne ",i+2," is rang")
            return None
    return a


def LireDCMTC():
    taille = int(sys.argv[2])
    CMTC = np.zeros((taille,taille))
    CMTD = np.zeros((taille,taille))
    for i in range(taille):
        for j in range(taille):
            CMTC[i][j]=float(sys.argv[j+taille*i+3])
            CMTD[i][j]=-CMTC[i][j]/ float(sys.argv[i+taille*i+3])
        CMTD[i][i]=0
    return CMTC,CMTD



def Lire_pi(taille):
    print("in")
    l = np.zeros((1,taille))
    got_one = False
    for i in range(taille):
        l[0][i] = float(input("give the next value"))
        while ((l[0][i] != 1) and (l [0][i] != 0)):
            l[i] = float(input("the value is rang , try an other one == 0 or == 1"))
        if l[0][i] == 1:
            if got_one :
                 print("the value is  range , u enter two one")
                 return None
            else:
                got_one = True
    return l



def eststochastique(P):
    nl = len(P)  # nombre de lignes
    nc = len(P[0])  # nombre de colonnes
    etat = True  # on suppose que la matrice est stochastique
    for i in range(nl):
        s = 0
        for j in range(nc):
            s += P[i][j]
        if s > 1:
            etat = False
            break
    return etat

def Pi_N(Pi,P,N):
    P_N=alg.matrix_power(P,N)
    res=np.dot(Pi, P_N)
    return res

def find_way(P,tupels):
    newTupels = []
    for i in (tupels):
        if P[i[0]][i[1]] == 0:
            newTupels.append(i)
    return newTupels

def is_irreductible(P):
    size = len(P[0])
    tupels = []
    for i in range(size):
        for j in range(size):
            tupels.append((i,j))
    for i in range(1,size+1):
        tupels =find_way(alg.matrix_power(P,i),tupels)
        if not tupels:
            print("La matrice est irreductible")
            return True
    print("la matrice n'est pas irreductible")
    return False

def CalculerPeriod(P):
    size = len(P[0])
    listdesperiod = []
    for i in range(size):
        period = CalculerPeriod_detat(P,i)
        print("etat numero ",i+1," est de period ",period)
        listdesperiod.append(period)
    return np.gcd.reduce(listdesperiod)


def CalculerPeriod_detat(P,indice):
    if P[indice][indice] != 0:
        return 1
    size = len(P[0])
    lis_pgcd = []
    for i in range(2, size + 1):
        mat = alg.matrix_power(P, i)
        if mat[indice][indice] != 0:
            lis_pgcd.append(i)
    return np.gcd.reduce(lis_pgcd)



def is_aperiodique(P):
    Period = CalculerPeriod(P)
    if Period == 1 :
        print("La matrice est aperiodique")
        return True
    else:
        print("La matrice est periodique de period :",Period)
        return False

def is_ergodique(P):
    if is_irreductible(P):
        if is_aperiodique(P):
            print("La matrice est ergodique")
            return True

    return False


def calculer_erreur(P1,P2):
    return np.max(np.abs(np.subtract(P1,P2)))


def regimeStationnaire(Pi,P,erreur):
    P = alg.matrix_power(P,10000)
    nbrfois = 1
    while nbrfois < 100000:
        Ptemp = alg.matrix_power(P, 2)
        if erreur > np.max(np.abs(np.subtract(P,Ptemp))):
            print("nombre de fois ", nbrfois)
            return np.dot(Pi, Ptemp)
        P = Ptemp
        nbrfois = nbrfois+1
    return np.dot(Pi , Ptemp)


def ResoudreCMTD(a):
    b=np.append(np.full((1, a[0].__len__()), 0), 1)
    a=np.append(np.subtract(a,np.identity(a[0].__len__())).transpose(),np.full((1, a[0].__len__()), 1),axis=0)
    Q, R = alg.qr(a)
    return spla.solve_triangular(R, Q.T.dot(b), lower=False)


def ResoudreCMTC(a):
    b = np.append(np.full((1, a[0].__len__()), 0), 1)
    a = np.append(a.transpose(), np.full((1, a[0].__len__()), 1), axis=0)
    Q, R = alg.qr(a)
    return spla.solve_triangular(R, Q.T.dot(b), lower=False)





if sys.argv[1] == "MATRICE_CMTD":
    CMTD = LireCMTD()
    if CMTD is not None:
        if is_ergodique(CMTD):
            print("il existe un regime stationnaire :")
            pi_n = ResoudreCMTD(CMTD)
            print("regime stationnaire ", pi_n)

elif sys.argv[1] == "MATRICE_CMTC":
    CMTC,CMTD = LireDCMTC()
    if is_irreductible(CMTD):
        print("il existe un regime stationnaire :")
        pi_n = ResoudreCMTC(CMTC)
        print("regime stationnaire ", pi_n)




