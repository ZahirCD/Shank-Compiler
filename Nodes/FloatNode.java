package ShankInterpreter;

public class FloatNode extends Node
{
	private float decimalNumber;
	/*
	 * Constructor
	 */
	public FloatNode()
	{
		decimalNumber = -1;
	}
	/*
	 * Parameterized Constructor
	 * val : float
	 */
	public FloatNode(float value)
	{
		decimalNumber = value;
	}
	/*
	 * return float value
	 */
	public float getVal()
	{
		return decimalNumber;
	}
	/*
	 * return number as a string 
	 */
	public String toString()
	{
		return Float.toString(decimalNumber);
	}











}
