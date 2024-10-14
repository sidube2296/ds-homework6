// This is an assignment for students to complete after reading Chapter 4 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.util.function.Consumer;


/******************************************************************************
 * This class is a homework assignment;
 * A Sequence is an aggregate class with a cursor (not an iterator)
 * The sequence can have a special "current element," which is specified and 
 * accessed through four methods
 * (start, getCurrent, advance and hasCurrent).
 *
 ******************************************************************************/
public class LinkedSequence<E> implements Cloneable
{
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	// TODO: Declare the private static generic Node class with fields data and next.
	// The class should be private, static and generic.
	// Please use a different name for its generic type parameter.
	// It should have a constructor or two (at least the default constructor) but no methods.
	// The no-argument constructor can construct a dummy node if you would like.
	// The fields of Node should have "default" access (neither public, nor private)
	private static class Node<T> {
		T data; //The data contained in the node
		Node<T> next; // The reference to the next node
		
		// Default constructor for a dummy node
		Node() {
            this.data = null;
            this.next = this;  // Point to itself
		}
		// Constructor with data and next node reference
		Node(T data, Node<T> next) {
			this.data = data;
			this.next = next;
		}
		
        }

	
	// TODO: Declare the private fields of Sequences:
	// One for the tail, one for the size and one for the precursor.
	// Do not declare any other fields.
	// In particular do *NOT* declare a "dummy" field.  The dummy should be a model field.
	
	private Node<E> tail;
	private Node<E> precursor;
	private int size;
	
	/// Model fields:
	// These are the conceptual fields that are computed from the concrete fields.
	// The getters are private: not for clients.  Don't assert invariant.
	// They are allowed to simply crash if the data structure is inconsistent.

	/** Return the head node from the data structure since we do not have a head field. */
	private Node<E> getHead() {
		if (tail == null) return null;
        return tail.next.next; 
	}
	
	/** Return the dummy node from the data structure since we do not have a dummy field. */
	private Node<E> getDummy() {
		return tail != null ? tail.next : null;
	}

	/** Return the cursor from the data structure since we do not have a cursor field. */
	private Node<E> getCursor() {
		if (precursor == null) return getHead();
        return precursor.next;
        
	}
	
	
	/**
	 * Check the invariant.  Report any problem precisely once.
	 * Return false if any problem is found.  Returning an informative
	 * {@link #report(String)} will make it easier to debug invariant problems.
	 * @return whether invariant is currently true
	 */
	private boolean wellFormed() {
		// Invariant:
		// 1. tail node is not null, and the dummy (next after tail) should not be null either.
		if (tail == null) {
			return report("Tail node is null");
		}
		if (tail.next == null || tail.next.data != tail.next) {
	        return report("Dummy node's data do not point to itself or is null");
		}
		if (tail.next == null) {
			return report("Node is not cyclic");
		}

		// 2. The dummy node's data should be itself.
		if (tail.next.data != null && tail.next.data != tail.next) {
			return report("Dummy node's data is incorrect");
		}

		// 3. list must be in the form of a cycle from tail back to tail
		Node<E> slow = tail.next;
		Node<E> fast = tail.next;
		
		while (slow != tail && fast.next != null && fast.next != tail && fast.next.next != null) {
			slow = slow.next;
			fast = fast.next.next;
			
			if (slow == fast) {
				break;
			}
		}

		if (fast != tail && fast.next != tail) {
			return report("List is not properly cyclic");
		}

		// 4. size is number of nodes in list, other than the dummy
		int count = 0;
		Node<E> current = tail.next;
		while (current != tail) {
			count++;
			current = current.next;
		}
		if (count != size) {
			return report("Size is not equal to the number of nodes");
		}

		// 5. precursor points to a node in the list (possibly the dummy).
		Node<E> check = tail.next;
		while (check != tail) {
			if (precursor == check) break;
			check = check.next;
		}
		if (precursor != tail && precursor != check) {
			return report("Precursor does not point to the node in the list");
		}
		// If no problems found, then return true:
		return true;
	}

	private LinkedSequence(boolean doNotUse) {} // only for purposes of testing, do not change
	
	/**
	 * Create an empty sequence.
	 * @param - none
	 * @postcondition
	 *   This sequence is empty 
	 **/   
	public LinkedSequence( )
	{
		// TODO: initialize data structure
		Node<E> dummy = new Node<E>(); // Creating a dummy node
		
		dummy.data = (E) dummy;
		tail=dummy;
		tail.next = dummy;
		precursor = dummy;
		
		size = 0; 

		assert wellFormed() : "invariant failed in constructor";
	}

	/**
	 * Determine the number of elements in this sequence.
	 * @param - none
	 * @return
	 *   the number of elements in this sequence
	 **/ 
	public int size( )
	{
		assert wellFormed() : "invariant wrong at start of size()";
		// TODO: Implemented by student.
		// This method shouldn't modify any fields, hence no assertion at end
		return size;
	}

	// TODO: All the sequence methods with documentation.
	// You may copy anything useful from Homework #4.
	// Just make sure that the code doesn't refer to "Ball".

	/**
	 * Move the cursor to the start of this cyclic linked list
	 * @param none
	 * @postcondition
	 *   The first element of this cyclic linked list is now the current element, which means
	 *   the cursor will point to the element after the dummy node (if the list is non-empty).
	 *   If the list is empty (i.e., only the dummy node exists), the precursor is moved to the dummy node.
	 *   The list maintains its cyclic structure, where the first element is located right after 
	 *   the tail's next reference.
	 **/
	public void start() {
		assert wellFormed() : "invariant wrong at start of start()";
		precursor = tail.next; // Move precursor to dummy node
		assert wellFormed() : "invariant wrong at end of start()";
    }

	/**
	 * Accessor method to determine whether this sequence has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @param - none
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent() {
		// TODO Auto-generated method stub
		assert wellFormed() : "invariant wrong at start of isCurrent()";
	    // If precursor exists and points to a valid current element (not dummy) return true
	    return ( precursor.next != tail.next);

	}
	
	/**
	 * Accessor method to get the current element of this sequence. 
	 * @param - none
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this sequence
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public E getCurrent() throws IllegalStateException {
		assert wellFormed() : "invariant worng at start of getCurrent()";
		// TODO Auto-generated method stub
		if (!isCurrent()) throw new IllegalStateException("No current element");
		return precursor.next.data; // Return data of the current node
    }
		
	/**
	 * Move the cursor forward to the next element in this cyclic linked list.
	 * @param none
	 * @precondition
	 *   isCurrent() must return true, meaning there is a current element.
	 * @postcondition
	 *   If the current element was the last element in the list (right before the dummy node),
	 *   the precursor will now point to dummynode, indicating there is no current element. 
	 *   Otherwise, the cursor will move to the element immediately after the current element.
	 *   The precursor is updated to follow the cursor.
	 * @exception IllegalStateException
	 *   Thrown if there is no current element, preventing the advance operation.
	 **/
	public void advance() {
		assert wellFormed() : "Invariant wrong at start of advance()";
		// TODO Auto-generated method stub
		if (!isCurrent()) throw new IllegalStateException("No current element to advance");
        precursor = precursor.next;
		assert wellFormed() : "Invariant wrong at end of advance()";
	}

	/**
	 * Remove the current element from this cyclic linked list.
	 * @param none
	 * @precondition
	 *   isCurrent() must return true, meaning there is a valid current element to remove.
	 * @postcondition
	 *   The current element has been removed from this list. The element immediately following 
	 *   the removed element (if one exists) becomes the new current element. 
	 *   If the removed element was the only element, or there is no following element, 
	 *   the cursor becomes null, indicating there is no current element. The list retains 
	 *   its cyclic structure after removal.
	 * @exception IllegalStateException
	 *   Thrown if there is no current element to remove, preventing the operation.
	 **/
	public void removeCurrent() {
		assert wellFormed() : "Invariant wrong at start of removeCurrent()";
		
		Node<E> current = precursor.next; 
		precursor.next = current.next;     
		size--;                           

		if (current == tail) {            
		    tail = precursor;              
		}

		if (size == 0) {                
		    tail.next = tail;             
		    precursor = tail;            
		}
		
		assert wellFormed() : "Invariant wrong at end of removeCurrent()";
	}
	
	/**
	 * Add a new element to this sequence, before the current element (if any). 
	 * If the new element would take this sequence beyond its current capacity,
	 * then the capacity is increased before adding the new element.
	 * @param element
	 *   the new element that is being added
	 * @postcondition
	 *   A new copy of the element has been added to this sequence. If there was
	 *   a current element, then the new element is placed before the current
	 *   element. If there was no current element, then the new element is placed
	 *   at the end of the sequence. In all cases, the new element becomes the
	 *   new current element of this sequence. 
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for increasing the sequence.
	 **/
	public void insert(E element) {
		assert wellFormed() : "Invariant failed at start of insert";
		// TODO Auto-generated method stub
		Node<E> newNode = new Node<>(element, getDummy());
        if (size == 0) {
            precursor.next = newNode;
            tail = newNode; 
        } else {
            newNode.next = precursor.next;
            precursor.next = newNode; 
           
            if (precursor == tail) {
                tail = newNode;
            }
        }
        size++;
		assert wellFormed() : "Invariant failed at end of insert";
	}
	/**
	 * Add a new element to this sequence, before the current element (if any). 
	 * If the new element would take this sequence beyond its current capacity,
	 * then the capacity is increased before adding the new element.
	 * @param element
	 *   the new element that is being added
	 * @postcondition
	 *   A new copy of the element has been added to this sequence. If there was
	 *   a current element, then the new element is placed before the current
	 *   element. If there was no current element, then the new element is placed
	 *   at the end of the sequence. In all cases, the new element becomes the
	 *   new current element of this sequence. 
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for increasing the sequence.
	 **/
	public void insertAll(LinkedSequence<E> se) {
		assert wellFormed() : "Invariant failed at start of insertAll";
		// TODO Auto-generated method stub
		assert wellFormed() : "Invariant failed at end of inserAll";	
		
	}

	/**
	 * Generate a copy of this sequence.
	 * @param - none
	 * @return
	 *   The return value is a copy of this sequence. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 *   Whatever was current in the original object is now current in the clone.
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for creating the clone.
	 **/ 
	@SuppressWarnings("unchecked")
	public LinkedSequence<E> clone( )
	{  	 
		assert wellFormed() : "invariant wrong at start of clone()";

		LinkedSequence<E> result;

		try
		{
			result = (LinkedSequence<E>) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  
			// This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}

		// TODO: Implemented by student.
		// Now do the hard work of cloning the list.
		// Similar to Homework #4, setting result.precursor requires an "if"
		// It's possible to handle the dummy without a second if or an unsafe cast.
		// but it's OK to have a second if and/or a cast to handle this.
		assert wellFormed() : "invariant wrong at end of clone()";
		assert result.wellFormed() : "invariant wrong for result of clone()";
		return result;
	}

	
	/**
	 * Class to assist internal testing of the data structure.
	 * Do not change this class!
	 * @param T the element type to use
	 */
	public static class Spy<T> {
		/**
		 * A public version of the data structure's internal node class.
		 * This class is only used for testing.
		 */
		public static class Node<U> extends LinkedSequence.Node<U> 
{
			/**
			 * Create a node with self data and next fields.
			 */
			@SuppressWarnings("unchecked")
			public Node() {
				this(null, null);
				this.data = (U)this;
				this.next = this;
			}
			/**
			 * Create a node with the given values
			 * @param d data for new node, may be null
			 * @param n next for new node, may be null
			 */
			public Node(U d, Node<U> n) {
				super();
				this.data = d;
				this.next = n;
			}
		}
		
		/**
		 * Create a node for testing.
		 * @param d data for new node, may be null
		 * @param n next for new node, may be null
		 * @return newly created test node
		 */
		public Node<T> newNode(T d, Node<T> n) {
			return new Node<T>(d, n);
		}
		
		/**
		 * Create a node with a self data and next fields for testing.
		 * @return newly created test node
		 */
		public Node<T> newNode() {
			return new Node<T>();
		}
		
		/**
		 * Change a node's data field
		 * @param n1 node to change, must not be null
		 * @param x value to set data field to
		 */
		@SuppressWarnings("unchecked")
		public void setData(Node<T> n1, Object x) {
			n1.data = (T)x;
		}
		
		/**
		 * Change a node's next field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setNext(Node<T> n1, Node<T> n2) {
			n1.next = n2;
		}
		
		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a testing instance of the ADT with the given
		 * data structure.
		 * @param t the tail node
		 * @param p the precursor
		 * @param s the size
		 * @return a new testing linked sequence with this data structure.
		 */
		public LinkedSequence<T> newInstance(Node<T> t, Node<T> p, int s) {
			LinkedSequence<T> result = new LinkedSequence<T>(false);
			result.tail = t;
			result.precursor = p;
			result.size = s;
			return result;
		}
			
		/**
		 * Check the invariant on the given dynamic array robot.
		 * @param r robot to check, must not be null
		 * @return whether the invariant is computed as true
		 */
		public boolean wellFormed(LinkedSequence<?> r) {
			return r.wellFormed();
		}
		
		/** 
		 * Return the head of the testing data structure.
		 */
		public Object getHead(LinkedSequence<?> r) {
			return r.getHead();
		}
		
		/** 
		 * Return the head of the testing data structure.
		 */
		public Object getDummy(LinkedSequence<?> r) {
			return r.getDummy();
		}
		
		/** 
		 * Return the head of the testing data structure.
		 */
		public Object getCursor(LinkedSequence<?> r) {
			return r.getCursor();
		}
	}

}
