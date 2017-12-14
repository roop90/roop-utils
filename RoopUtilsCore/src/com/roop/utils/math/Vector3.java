package com.roop.utils.math;

/**
 * Created with IntelliJ IDEA.
 * Project: roop-utils
 * User: roop
 * Date: 30.04.2016
 * Time: 22:36
 * Copyright: Ralf Wiedemann
 */
public class Vector3 {

	private double mX;
	private double mY;
	private double mZ;

	public void setX(double x){
		this.mX = x;
	}
	public void setY(double y){
		this.mY = y;
	}
	public void setZ(double z){
		this.mZ = z;
	}
	public void set(Vector3 vec){
		this.mX = vec.getX();
		this.mY = vec.getY();
		this.mZ = vec.getZ();
	}

	public double getX(){
		return mX;
	}
	public double getY(){
		return mY;
	}
	public double getZ(){
		return mZ;
	}

	public Vector3(){
		this(0);
	}

	public Vector3(double x){
		this(x, 0);
	}

	public Vector3(double x, double y){
		this(x, y, 0);
	}

	public Vector3(Vector3 vec){
		this(vec.getX(), vec.getY(), vec.getZ());
	}

	public Vector3(double x, double y, double z){
		mX = x;
		mY = y;
		mZ = z;
	}

	/**
	 * Returns the length of this vector
	 * @return
	 */
	public double length(){
		return Math.sqrt(this.scalar(this));
	}

	/**
	 * Sets value of this Vector3 to (this + vec)
	 * @param vec
	 */
	public void add(Vector3 vec){
		this.mX += vec.getX();
		this.mY += vec.getY();
		this.mZ += vec.getZ();
	}

	/**
	 * Retruns a new Vector3 with value of (this + vec)
	 * @param vec
	 * @return
	 */
	public Vector3 added(Vector3 vec) {
		return new Vector3(this.mX + vec.mX, this.mY + vec.mY, this.mZ + vec.mZ);
	}

	/**
	 * Sets value of this Vector3 to (this - vec)
	 * @param vec
	 */
	public void sub(Vector3 vec){
		this.mX -= vec.getX();
		this.mY -= vec.getY();
		this.mZ -= vec.getZ();
	}

	/**
	 * Retruns a new Vector3 with value of (this - vec)
	 * @param vec
	 * @return
	 */
	public Vector3 subed(Vector3 vec) {
		return new Vector3(this.mX - vec.mX, this.mY - vec.mY, this.mZ - vec.mZ);
	}

	/**
	 * Sets value of this Vector3 to (this * val)
	 * @param val
	 */
	public void scale(double val){
		this.mX*=val;
		this.mY*=val;
		this.mZ*=val;
	}

	/**
	 * Retruns a new Vector3 with value of (this * val)
	 * @param val
	 * @return
	 */
	public Vector3 scaled(double val){
		return new Vector3(this.mX*val, this.mY*val, this.mZ*val);
	}

	/**
	 * Returns the scalar product of this and vec
	 * @param vec
	 * @return
	 */
	public double scalar(Vector3 vec){
		return this.mX*vec.getX()+this.mY*vec.getY()+this.mZ*vec.getZ();
	}


	/**
	 * Returns the cross product of this and vec
	 * @param vec
	 * @return
	 */
	public Vector3 cross(Vector3 vec){
		return new Vector3(this.mY*vec.getZ()-this.mZ*vec.getY(), this.mZ*vec.getX()-this.mX*vec.getZ(), this.mX*vec.getY()-this.mY*vec.getX());
	}

	/**
	 * Normalizes this vector to length 1
	 */
	public void normal(){
		this.scale(1/this.length());
	}

	@Override
	public String toString() {
		return "X:" + this.mX + " Y:" + this.mY + " Z:" + this.mZ;
	}

	/**
	 * Returns the angle between vector a and b in degree
	 * @param a
	 * @param b
	 * @return
	 */
	public static double angleBetween(Vector3 a, Vector3 b){
		return Math.acos(a.scalar(b)/(a.length()*b.length()))*180/Math.PI;
	}

	/**
	 * Returns the angle between vector a and b in degree
	 * @param a
	 * @param b
	 * @return
	 */
	public static double angleBetweenRad(Vector3 a, Vector3 b){
		return Math.acos(a.scalar(b)/(a.length()*b.length()));
	}
}
