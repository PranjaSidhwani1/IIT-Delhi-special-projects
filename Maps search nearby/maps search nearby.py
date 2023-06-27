class node():
    def __init__(self,key):
        self.key=key
        self.right=None
        self.left=None
        self.y_sort=[]
def AVL(L):
    if not L:
        return None
    mid=len(L)//2
    node=Treenode(L[mid])
    node.left=AVL(L[:mid])
    node.right=AVL(L[mid+1:])
    return node 
def AVL2(L):
    x=sorted(L)
    n=len(L)
    y=sorted(L,key=lambda x:x[1])
    return x,y

def Final_Tree(L,M):
    if len(L)!=len(M):
        print(-1)
    if len(L)==0:
        return None
    if len(L)==1:
        p=node(L[0])
        p.y_sort=[L[0]]
        return p
    else:
        root=node(L[len(L)//2])
        root.y_sort=M
        l=[]
        r=[]
        for i in M:
            if i[0]<L[len(L)//2][0]:
                l.append(i)
            elif i[0]>L[len(L)//2][0]:
                r.append(i)
        root.left=Final_Tree(sliced(L,0,len(L)//2),l)
        root.right=Final_Tree(sliced(L,len(L)//2+1,len(L)),r)
        return root

    
def y_small(L,y):
    if len(L)==0:
        return None
    else:
        s=0;l=len(L)-1
        while (l-s)>1:
            mid=(l+s)//2
            if L[mid][1]>=y:
                l=mid
            else:
                s=mid
        if L[l][1]>=y:
            return l        
        elif L[s][1]>=y:
            return s
        else:
            return None

def y_large(L,y):
    if len(L)==0:
        return None
    else:
        s=0
        l=len(L)-1
        while (l-s)>1:
            mid=(l+s)//2
            if L[mid][1]>y: 
                l=mid
            else:
                s=mid
        if L[l][1]<=y:
            return l
        elif l[s][1]<=y:
            return s
        else:
            return None

def y_range(L,s,l):
    count=[]
    small=y_small(L,s);
    large=y_large(L,l)
    if small==None or large==None:
        return []
    else:
        for i in range (small,large+1):
            count.append(L[i])
        return count
    
def sliced(L,i,j):
    count=[]
    for i in range (i,j):
        count.append(L[i])
    return count
def IsLeaf(node):
    if node==None:
        return False
    if node.left==None and node.right==None:
        return True
    return False
def x_range(node,s,l):
    if node==None:
        return None
    p=node
    while IsLeaf(p)!=True and p!=None:
        if p.key[0]<=l and p.key[0]>=s:
            return p
            break
        else:
            if p.key[0]<=s:
                p=p.right
            else:
                p=p.left
    if p==None:
        return None
    if p.key[0]<=l and p.key[0]>=s:
        return p
    else:
        return None
def pre_search(node,x1,x2,y1,y2):
    ans=[]
    p=x_range(node,x1,x2)
    if p==None:
        return ans
    l=p.left
    r=p.right
    if IsLeaf(p):
        if p.key[0]>=x1 and p.key[0]<=x2 and p.key[1]>=y1 and p.key[1]<=y2:
            ans.append(p.key)
            return [p.key]
        else:
            return []
    else:
        if p.key[0]>=x1 and p.key[0]<=x2:
            if p.key[1]>=y1 and p.key[1]<=y2:
                ans.append(p.key)
        while l!=None:
            if l.key[0]>=x1 and l.key[0]<=x2 and l.key[1]>=y1 and l.key[1]<=y2:
                ans.append(l.key)
            if l.key[0]>=x1:
                if l.right!=None:
                    for i in y_range(l.right.y_sort,y1,y2):
                        ans.append(i)
                l=l.left
            else:
                l=l.right
        while r!=None:
            if r.key[0]>=x1 and r.key[0]<=x2 and r.key[1]>=y1 and r.key[1]<=y2:
                ans.append(r.key)
            if r.key[0]<=x2:
                if r.left!=None:
                    for i in y_range(r.left.y_sort,y1,y2):
                        ans.append(i)
                r=r.right
            else:
                r=r.left
        return ans

class PointDatabase:
    def __init__(self,L):
        L=AVL2(L)[0]
        M=AVL2(L)[1]
        self.tree=Final_Tree(L,M)
    def searchNearby(self,q,d):
        return pre_search(self.tree,q[0]-d,q[0]+d,q[1]-d,q[1]+d)

# pointDbObject=PointDatabase([(1,6), (2,4), (3,7), (4,9), (5,1), (6,3), (7,8), (8,10),(9,2), (10,5)])
# print(pointDbObject.searchNearby((5,5), 1))
# print(pointDbObject.searchNearby((4,8), 2))
# print(pointDbObject.searchNearby((10,2), 1.5))




