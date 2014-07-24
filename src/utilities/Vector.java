package utilities;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.PI;
import geomerative.RPoint;
import geomerative.RShape;

import java.util.ArrayList;
import java.util.List;

import processing.core.PShape;
import processing.core.PVector;


/*** A class to contain useful static methods for manipulating vectors. ***/
public class Vector {
	
	public static RShape pToRShape(PShape shape) {
		int vertexCount = shape.getVertexCount();
		if (vertexCount == 0) {
			throw new IllegalArgumentException("Input shape contains no vertices, could not compute RShape");
		}
		RShape s = new RShape();
		PVector start = shape.getVertex(0);
		s.addMoveTo(start.x, start.y);
		PVector point = new PVector();
		for (int i = 1; i < vertexCount; i ++) {
			shape.getVertex(i, point);
			s.addLineTo(point.x, point.y);
		}
		s.addLineTo(start.x,start.y);
		s.addClose();
		return s;	
	}
	
	/*** tests if the two vectors cross one-another. ***/
	public static boolean intersects(RPoint p, RPoint r, RPoint q, RPoint s) {
		float rXs = r.x*s.y - r.y*s.x;
		if (rXs == 0) {return false;} //lines are parrelel (note I don't care if colinear)
		RPoint qMinusP = new RPoint(q.x - p.x,q.y - p.y);
		float t = (qMinusP.x*s.y - qMinusP.y*s.x)/rXs;
		float u = (qMinusP.x*r.y - qMinusP.y*r.x)/rXs;
		if (t < 1 && u < 1 && t > 0 && u > 0){
			return true;
		}
		return false;
	}

	/*** Takes an input PShape, converts it to an RShape and shifts the outline outwards along the normal by the specified shift. ***/
	public static RShape expand(PShape s, float shift) {
		if (shift <= 0) {throw new IllegalArgumentException("shift must be > 0");}
		RShape tmp = pToRShape(s);
		RPoint start = null;
		RPoint lastLine = null;
		RPoint lastPoint = null;
		int skipped = 0;
		RShape output = new RShape();
		for (int v = 0; v < s.getVertexCount(); v ++) {
			float adv = v/(float)s.getVertexCount();
			RPoint tangent = tmp.getTangent(adv);
			RPoint p = tmp.getPoint(adv);
			RPoint norm = Vector.getUnitNorm(tangent);
			norm.scale(shift);
			RPoint outSidePoint = new RPoint(p.x-norm.x, p.y-norm.y);
			if (start == null) { //adding the first point
				start = outSidePoint;			
				output.addMoveTo(start);
				lastPoint = start;

			} else if (lastLine == null) {//adding the second point (ie the first line)
				output.addLineTo(outSidePoint);
				lastLine = new RPoint(outSidePoint.x - lastPoint.x, outSidePoint.y - lastPoint.y);
				lastPoint = outSidePoint;				

			} else {
				RPoint thisLine = new RPoint(outSidePoint.x - lastPoint.x, outSidePoint.y - lastPoint.y);
				float angle = angle(thisLine,lastLine);
				if (skipped > 3 || angle < PI/2 || angle > 3*PI/2) {
					output.addLineTo(outSidePoint);
					lastLine = thisLine;
					lastPoint = outSidePoint;
					skipped = 0;
				} else {
					skipped ++;
					System.out.println("skipped");
				}				
			}
		}
		output.addLineTo(start);
		output.addClose();
		return output;
	}
	
	/*** Gets the unit norm vector to a surface given the tangent vector at that point.
	 * So to draw the unit normal you would draw a line from x,y to x+n.x,y+n.y. ***/
	public static final RPoint getUnitNorm(RPoint tangent) {
		tangent.normalize();
		return new RPoint(tangent.y,-tangent.x);
	}
	
	/*** create an RPoint vector at the specified radius and angle from the input origin. ***/
	public static RPoint radialPoint(double radius, double angle, RPoint origin) {
		RPoint p = new RPoint(radius*Math.cos(angle) + origin.x, radius*Math.sin(angle) + origin.y);
		return p;
	}
	
	/*** Create an array of angles of length nPoints, equally spaced from 0 to 2PI. ***/
	public static List<Float> createEvenAngles(int nPoints) {
		List<Float> result = new ArrayList<Float>(nPoints);
		float theta = 0;
		float stepSize = PI*2/((float)nPoints);
		for (int i = 0; i < nPoints; i++) {
			result.add(theta);
			theta += stepSize;
		}
		return result;
	}
	
	/*** return a point the specified percentage along the input line. ***/
	public static RPoint getPoint(float percentage, float x1, float y1, float x2, float y2){
		float dx = (x2 - x1);
		float dy = (y2 - y1);
		float dx2 = percentage*dx;
		float dy2 = percentage*dy;
		return new RPoint(x1+dx2, y1+dy2);
	}
	
	/*** returns true if p1 is clockwise from p2 otherwise false. ***/
	public static boolean clockwise(RPoint p1, RPoint p2) {
		float theta1 = (float) Math.atan2(p1.y, p1.x); //a number between -PI and PI
		float theta2 = (float) Math.atan2(p2.y, p2.x);
		float diff = theta2 - theta1;
		float diffMag = Math.abs(diff);
		if (diffMag >= PI) {
			diff = - diff;
		}
		if (diff < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/*** returns the angle between two points in radians (0 <= theta < 2PI) ***/
	public static float angle(RPoint p1, RPoint p2) {
		float theta1 = (float) Math.atan2(p1.y, p1.x); //a number between -PI and PI
		float theta2 = (float) Math.atan2(p2.y, p2.x);
		float diff = theta2 - theta1;
		if (diff <0) {diff += 2*PI;} 
		return diff;
	}
}
