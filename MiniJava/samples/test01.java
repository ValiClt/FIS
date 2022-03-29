//java coment
/* test mutiline coment
 * 
 * is beautiful
 */

class Main
{
 public static void main(String[] args)
 {
  System.out.println(new TestShape().test());
 }
}

class Shape
{
 int x;
 int y;

 public int print()
 {
	return 0;
 }

 public int translate (int dx, int dy)
 {
  x = x + dx;
  y = y + dy;
  return 0;
 }
}

class Rectangle extends Shape
{
 int width;
 int height;

 public Shape f(int x, int y, Shape z) {
	 return new Shape();
 }
 
 public int print()
 {
  System.out.println(x);
  System.out.println(y);
  System.out.println(width);
  System.out.println(height);
  return 0;
 }
}

class Circle extends Shape
{
 int radius;

 public int print()
 {
  System.out.println(x);
  System.out.println(y);
  System.out.println(radius);
  return 0;
 }
 
 public Circle getCircle() {
		return new Circle();
	}
 
 public int[] last(int x) {
	 return new int[x];
 }
 
}

class TestShape
{
	public Circle getCircle() {
		return new Circle();
	}
	
public int test()
 {
  Shape s;
  Rectangle r;
  Circle c;
  int[] v;
  
  v = new int[3];
  
  v[1]=2*4+5;
  v.length;

  r=new Rectangle();
  v[0]=this.getCircle().getCircle().last(5)[3];

  s=r;
  s=c;

  v[2] = s.print();
  s.translate(1,1);
  s.print();

  c.print();
  c.translate(2,2);
  c.print();
  
  r.f(1, 2, s);

  return 0;
 }
}
