package javax.persistence.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Factory object for queries, select lists, restrictions, 
 * expressions, orderings
 * The factory methods are all typesafe. 
 * Note that Predicate is used instead of Expression<Boolean> 
 * in this API in order to work around the fact that Java 
 * generics are not compatible with varags.
 */
public interface QueryBuilder {

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    CriteriaQuery create();

	
    //select new Foo(...) equivalent:

    /**
     * Define a select list item corresponding to a constructor.
     * @param result  class whose instance is to be constructed
     * @param selections  arguments to the constructor
     * @return selection item
     */
    <Y> Selection<Y> select(Class<Y> result, Selection<?>... selections);
	

    //ordering:
	
    /**
     * Create an ordering by the ascending value of the expression.
     * @param x  expression used to define the ordering
     * @return ascending ordering corresponding to the expression
     */
    Order asc(Expression<?> x);

    /**
     * Create an ordering by the descending value of the expression.
     * @param x  expression used to define the ordering
     * @return descending ordering corresponding to the expression
     */
    Order desc(Expression<?> x);

	
    //aggregate functions:
	
    /**
     * Create an expression applying the avg operation.
     * @param x  expression representing input value to avg operation
     * @return avg expression
     */
    <N extends Number> Expression<N> avg(Expression<N> x);

    /**
     * Create an expression applying the sum operation.
     * @param x  expression representing input value to sum operation
     * @return sum expression
     */
    <N extends Number> Expression<N> sum(Expression<N> x);
    
    /**
     * Create an expression applying the max operation.
     * @param x  expression representing input value to max operation
     * @return max expression
     */
    <N extends Number> Expression<N> max(Expression<N> x);
    
    /**
     * Create an expression applying the min operation.
     * @param x  expression representing input value to min operation
     * @return min expression
     */
    <N extends Number> Expression<N> min(Expression<N> x);
	
    /**
     * Create an expression applying the count operation.
     * @param x  expression representing input value to count 
     * 				operation
     * @return count expression
     */
    Expression<Integer> count(Expression<?> x);

    /**
     * Create an expression applying the count distinct operation.
     * @param x  expression representing input value to 
     *        count distinct operation
     * @return count distinct expression
     */
    Expression<Integer> countDistinct(Expression<?> x);
	


    //subqueries:
	
    /**
     * Create a predicate testing the existence of a subquery result.
     * @param subquery  subquery whose result is to be tested
     * @return exists predicate
     */
    Predicate exists(Subquery<?> subquery);
	
    /**
     * Create a predicate corresponding to an all expression over the
     * subquery results.
     * @param subquery
     * @return all expression
     */
    <Y> Expression<Y> all(Subquery<Y> subquery);
	
    // Not sure what this does.  Why is this needed?
    <Y> Expression<Y> none(Subquery<Y> subquery);
	
    /**
     * Create a predicate corresponding to a some expression over the
     * subquery results.  This is equivalent to an any expression.
     * @param subquery
     * @return all expression
     */
    <Y> Expression<Y> some(Subquery<Y> subquery);
	
    /**
     * Create a predicate corresponding to an any expression over the
     * subquery results.  This is equivalent to a some expression.
     * @param subquery
     * @return any expression
     */	
    <Y> Expression<Y> any(Subquery<Y> subquery);


    //boolean functions:
	
    /**
     * Create a conjunction of the given boolean expressions.
     * @param x  boolean expression
     * @param y  boolean expression
     * @return and predicate
     */
    Predicate and(Expression<Boolean> x, Expression<Boolean> y);

    /**
     * Create a disjunction of the given boolean expressions.
     * @param x  boolean expression
     * @param y  boolean expression
     * @return or predicate
     */
    Predicate or(Expression<Boolean> x, Expression<Boolean> y);
    
    /**
     * Create a conjunction of the given restriction predicates.
     * @param restriction  zero or more restriction predicates
     * @return and predicate
     */
    Predicate and(Predicate... restrictions);

    /**
     * Create a conjunction of the given restriction predicates.
     * @param restriction  zero or more restriction predicates
     * @return and predicate
     */
    Predicate or(Predicate... restrictions);

    /**
     * Create a negation of the given restriction. 
     * @param restriction  restriction expression
     * @return not predicate
     */
    Predicate not(Expression<Boolean> restriction);
	
    /**
     * Create a conjunction (with zero conjuncts).
     * @return and predicate
     */
    Predicate conjunction();

    /**
     * Create a disjunction (with zero disjuncts).
     * @return or predicate
     */
    Predicate disjunction();

	
    //turn Expression<Boolean> into a Predicate
    //useful for use with varargs methods	

    /**
     * Create a predicate testing for a true value.
     * @param x  expression to be tested if true
     * @return predicate
     */
    Predicate isTrue(Expression<Boolean> x);

    /**
     * Create a predicate testing for a false value.
     * @param x  expression to be tested if false
     * @return predicate
     */
    Predicate isFalse(Expression<Boolean> x);

	
    //equality:
	
    /**
     * Create a predicate for testing the arguments for equality.
     * @param x  expression
     * @param y  expression
     * @return equality predicate
     */
    Predicate equal(Expression<?> x, Expression<?> y);

    /**
     * Create a predicate for testing the arguments for inequality.
     * @param x  expression
     * @param y  expression
     * @return inequality predicate
     */
    Predicate notEqual(Expression<?> x, Expression<?> y);
	
    /**
     * Create a predicate for testing the arguments for equality.
     * @param x  expression
     * @param y  object
     * @return equality predicate
     */
    Predicate equal(Expression<?> x, Object y);
	
    /**
     * Create a predicate for testing the arguments for inequality.
     * @param x  expression
     * @param y  object
     * @return inequality predicate
     */
    Predicate notEqual(Expression<?> x, Object y);

    //Predicate in(Expression<?> expression, Object... values);
	
    //<Y> Predicate equal(Expression<Y> x, Expression<Y> y);
    //<Y> Predicate notEqual(Expression<Y> x, Expression<Y> y);
    //<Y> Predicate equal(Expression<Y> x, Y y);
    //<Y> Predicate notEqual(Expression<Y> x, Y y);
    //Predicate eq(Expression<? extends Number> x, Expression<? extends Number> y);
    //Predicate ne(Expression<? extends Number> x, Expression<? extends Number> y);
    //Predicate eq(Expression<? extends Number> x, Number y);
    //Predicate ne(Expression<? extends Number> x, Number y);
	
    //comparisons for generic (non-numeric) operands:

    /**
     * Create a predicate for testing whether the first argument is 
     * greater than the second.
     * @param x  expression
     * @param y  expression
     * @return greater-than predicate
     */	
    <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x, Expression<? extends Y> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than the second.
     * @param x  expression
     * @param y  expression
     * @return less-than predicate
     */	
    <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x, Expression<? extends Y> y);
    
    /**
     * Create a predicate for testing whether the first argument is 
     * greater than or equal to the second.
     * @param x  expression
     * @param y  expression
     * @return greater-than-or-equal predicate
     */	
    <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y);
	
    /**
     * Create a predicate for testing whether the first argument is 
     * less than or equal to the second.
     * @param x  expression
     * @param y  expression
     * @return less-than-or-equal predicate
     */	
    <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * between the second and third arguments in value.
     * @param v  expression 
     * @param x  expression
     * @param y  expression
     * @return between predicate
     */	
    <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v, Expression<? extends Y> x, Expression<? extends Y> y);
	
    /**
     * Create a predicate for testing whether the first argument is 
     * greater than the second.
     * @param x  expression
     * @param y  value
     * @return greater-than predicate
     */	
    <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x, Y y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than the second.
     * @param x  expression
     * @param y  value
     * @return less-than predicate
     */	
    <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x, Y y);

    /**
     * Create a predicate for testing whether the first argument is 
     * greater than or equal to the second.
     * @param x  expression
     * @param y  value
     * @return greater-than-or-equal predicate
     */	
    <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Y y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than or equal to the second.
     * @param x  expression
     * @param y  value
     * @return less-than-or-equal predicate
     */	
    <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y);

    /**
     * Create a predicate for testing whether the first argument is 
     * between the second and third arguments in value.
     * @param v  expression 
     * @param x  value
     * @param y  value
     * @return between predicate
     */	
    <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v, Y x, Y y);
	

    //comparisons for numeric operands:
	
    /**
     * Create a predicate for testing whether the first argument is 
     * greater than the second.
     * @param x  expression
     * @param y  expression
     * @return greater-than predicate
     */	
    Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than the second.
     * @param x  expression
     * @param y  expression
     * @return less-than predicate
     */	
    Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * greater than or equal to the second.
     * @param x  expression
     * @param y  expression
     * @return greater-than-or-equal predicate
     */	
    Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than or equal to the second.
     * @param x  expression
     * @param y  expression
     * @return less-than-or-equal predicate
     */	
    Predicate le(Expression<? extends Number> x, Expression<? extends Number> y);

    /**
     * Create a predicate for testing whether the first argument is 
     * greater than the second.
     * @param x  expression
     * @param y  value
     * @return greater-than predicate
     */		
    Predicate gt(Expression<? extends Number> x, Number y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than the second.
     * @param x  expression
     * @param y  value
     * @return less-than predicate
     */	
    Predicate lt(Expression<? extends Number> x, Number y);

    /**
     * Create a predicate for testing whether the first argument is 
     * greater than or equal to the second.
     * @param x  expression
     * @param y  value
     * @return greater-than-or-equal predicate
     */	
    Predicate ge(Expression<? extends Number> x, Number y);

    /**
     * Create a predicate for testing whether the first argument is 
     * less than or equal to the second.
     * @param x  expression
     * @param y  value
     * @return less-than-or-equal predicate
     */	
    Predicate le(Expression<? extends Number> x, Number y);
	

    //numerical operations:
	
    /**
     * Create an expression that returns the arithmetic negation
     * of its argument.
     * @param x expression
     * @return negated expression
     */
    <N extends Number> Expression<N> neg(Expression<N> x);

    /**
     * Create an expression that returns the absolute value
     * of its argument.
     * @param x expression
     * @return absolute value
     */
    <N extends Number> Expression<N> abs(Expression<N> x);
	

    //TODO: support builder pattern for sums, products

    /**
     * Create an expression that returns the sum
     * of its arguments.
     * @param x expression
     * @param y expression
     * @return sum
     */
    <N extends Number> Expression<N> sum(Expression<? extends N> x, Expression<? extends N> y);

    /**
     * Create an expression that returns the product
     * of its arguments.
     * @param x expression
     * @param y expression
     * @return product
     */
    <N extends Number> Expression<N> prod(Expression<? extends N> x, Expression<? extends N> y);

    /**
     * Create an expression that returns the difference
     * between its arguments.
     * @param x expression
     * @param y expression
     * @return difference
     */
    <N extends Number> Expression<N> diff(Expression<? extends N> x, Expression<? extends N> y);
	
    /**
     * Create an expression that returns the sum
     * of its arguments.
     * @param x expression
     * @param y value
     * @return sum
     */
    <N extends Number> Expression<N> sum(Expression<? extends N> x, N y);

    /**
     * Create an expression that returns the product
     * of its arguments.
     * @param x expression
     * @param y value
     * @return product
     */
    <N extends Number> Expression<N> prod(Expression<? extends N> x, N y);

    /**
     * Create an expression that returns the difference
     * between its arguments.
     * @param x expression
     * @param y value
     * @return difference
     */
    <N extends Number> Expression<N> diff(Expression<? extends N> x, N y);

    /**
     * Create an expression that returns the sum
     * of its arguments.
     * @param x value
     * @param y expression
     * @return sum
     */
    <N extends Number> Expression<N> sum(N x, Expression<? extends N> y);

    /**
     * Create an expression that returns the product
     * of its arguments.
     * @param x value
     * @param y expression
     * @return product
     */
    <N extends Number> Expression<N> prod(N x, Expression<? extends N> y);

    /**
     * Create an expression that returns the difference
     * between its arguments.
     * @param x value
     * @param y expression
     * @return difference
     */
    <N extends Number> Expression<N> diff(N x, Expression<? extends N> y);
	
    /**
     * Create an expression that returns the quotient
     * of its arguments.
     * @param x expression
     * @param y expression
     * @return quotient
     */
    Expression<Number> quot(Expression<? extends Number> x, Expression<? extends Number> y);

    /**
     * Create an expression that returns the quotient
     * of its arguments.
     * @param x expression
     * @param y value
     * @return quotient
     */
    Expression<Number> quot(Expression<? extends Number> x, Number y);

    /**
     * Create an expression that returns the quotient
     * of its arguments.
     * @param x value
     * @param y expression
     * @return quotient
     */
    Expression<Number> quot(Number x, Expression<? extends Number> y);
	
    /**
     * Create an expression that returns the modulus
     * of its arguments.
     * @param x expression
     * @param y expression
     * @return modulus
     */
    Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y);
	
    /**
     * Create an expression that returns the modulus
     * of its arguments.
     * @param x expression
     * @param y value
     * @return modulus
     */
    Expression<Integer> mod(Expression<Integer> x, Integer y);

    /**
     * Create an expression that returns the modulus
     * of its arguments.
     * @param x value
     * @param y expression
     * @return modulus
     */
    Expression<Integer> mod(Integer x, Expression<Integer> y);

    /**
     * Create an expression that returns the square root
     * of its argument.
     * @param x expression
     * @return modulus
     */	
    Expression<Double> sqrt(Expression<? extends Number> x);

	
    //typecasts:
    
    /**
     * Typecast
     * @param numeric expression
     * @return Expression<Long>
     */	
    Expression<Long> toLong(Expression<? extends Number> number);

    /**
     * Typecast
     * @param numeric expression
     * @return Expression<Integer>
     */	
    Expression<Integer> toInteger(Expression<? extends Number> number);

    /**
     * Typecast
     * @param numeric expression
     * @return Expression<Float>
     */	
    Expression<Float> toFloat(Expression<? extends Number> number);

    /**
     * Typecast     * @param numeric expression
     * @return Expression<Double>
     */	
    Expression<Double> toDouble(Expression<? extends Number> number);

    /**
     * Typecast
     * @param numeric expression
     * @return Expression<BigDecimal>
     */	
    Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number);

    /**
     * Typecast
     * @param numeric expression
     * @return Expression<BigInteger>
     */	
    Expression<BigInteger> toBigInteger(Expression<? extends Number> number);
	
    /**
     * Typecast
     * @param character expression
     * @return Expression<String>
     */	
    Expression<String> toString(Expression<Character> character);

	
    //literals:

    /*
     * Create an expression literal
     * @param value
     * @return expression literal
     */
    <T> Expression<T> literal(T value);


    //parameters:

    /*
     * Create a parameter.
     * @param parameter class
     * @return parameter
     */
    <T> Parameter<T> parameter(Class<T> paramClass);


    //collection operations:
	
    /**
     *  Create a predicate that tests whether a collection is empty.
     *  @param collection expression
     *  @return predicate
     */
    <C extends Collection<?>> Predicate isEmpty(Expression<C> collection);

    /**
     *  Create a predicate that tests whether a collection is
     *  not empty.
     *  @param collection expression
     *  @return predicate
     */
    <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection);
	
    /**
     * Create an expression that tests the size of a collection.
     * @param collection
     * @return size expression
     */ 
    <C extends Collection<?>> Expression<Integer> size(C collection);

    /**
     * Create an expression that tests the size of a collection.
     * @param collection
     * @return size expression
     */ 
    <C extends java.util.Collection<?>> Expression<Integer> size(Expression<C> collection);

    /**
     *  Create a predicate that tests whether an element is
     *  a member of a collection.
     *  @param elem element
     *  @param collection expression
     *  @return predicate
     */
    <E, C extends Collection<E>> Predicate isMember(E elem, Expression<C> collection);

    /**
     *  Create a predicate that tests whether an element is
     *  not a member of a collection.
     *  @param elem element
     *  @param collection expression
     *  @return predicate
     */
    <E, C extends Collection<E>> Predicate isNotMember(E elem, Expression<C> collection);
	
    /**
     *  Create a predicate that tests whether an element is
     *  a member of a collection.
     *  @param elem elementexpression
     *  @param collection expression
     *  @return predicate
     */
    <E, C extends Collection<E>> Predicate isMember(Expression<E> elem, Expression<C> collection);

    /**
     *  Create a predicate that tests whether an element is
     *  not a member of a collection.
     *  @param elem elementexpression
     *  @param collection expression
     *  @return predicate
     */
    <E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem, Expression<C> collection);
	

    //get the values and keys collections of the Map, which may then
    //be passed to size(), isMember(), isEmpty(), etc

    /**
     * Create an expression that returns the values of a map.
     * @param map
     * @return collection expression
     */
    <V, M extends Map<?, V>> Expression<Collection<V>> values(M map);

    /**
     * Create an expression that returns the keys of a map.
     * @param map
     * @return set expression
     */
    <K, M extends Map<K, ?>> Expression<Set<K>> keySet(M map);

	
    //string functions:
	
    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @return like predicate
     */
    Predicate like(Expression<String> x, Expression<String> pattern);
	
    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @param escapeChar  escape character expression
     * @return like predicate
     */
    Predicate like(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar);
	
    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @param escapeChar  escape character
     * @return like predicate
     */
    Predicate like(Expression<String> x, Expression<String> pattern, char escapeChar);
	
    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @return like predicate
     */
    Predicate like(Expression<String> x, String pattern);
	
    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @param escapeChar  escape character expression
     * @return like predicate
     */
    Predicate like(Expression<String> x, String pattern, Expression<Character> escapeChar);

    /**
     * Create a predicate for testing whether the expression
     * satisfies the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @param escapeChar  escape character
     * @return like predicate
     */
    Predicate like(Expression<String> x, String pattern, char escapeChar);
	
    /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, Expression<String> pattern);

    /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @param escapeChar  escape character expression
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar);

    /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string expression
     * @param escapeChar  escape character
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, Expression<String> pattern, char escapeChar);
	
    /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, String pattern);

    /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @param escapeChar  escape character expression
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, String pattern, Expression<Character> escapeChar);
	
   /**
     * Create a predicate for testing whether the expression
     * does not satisfy the given pattern.
     * @param x  string expression
     * @param pattern  string 
     * @param escapeChar  escape character
     * @return like predicate
     */
    Predicate notLike(Expression<String> x, String pattern, char escapeChar);

    /**
     *  String concatenation operation.
     *  @param x  string expression
     *  @param y  string expression
     *  @return expression corresponding to concatenation
     */
    Expression<String> concat(Expression<String> x, Expression<String> y);
	
    /**
     *  String concatenation operation.
     *  @param x  string expression
     *  @param y  string 
     *  @return expression corresponding to concatenation
     */
    Expression<String> concat(Expression<String> x, String y);

    /**
     *  String concatenation operation.
     *  @param x  string 
     *  @param y  string expression
     *  @return expression corresponding to concatenation
     */	
    Expression<String> concat(String x, Expression<String> y);
	
    /**
     *  Substring extraction operation.
     *  Extracts a substring starting at specified position through
     *  to end of the string.
     *  First position is 1.
     *  @param x  string expression
     *  @param from  start position expression 
     *  @return expression corresponding to substring extraction
     */	
    Expression<String> substring(Expression<String> x, Expression<Integer> from);
	
    /**
     *  Substring extraction operation.
     *  Extracts a substring starting at specified position through
     *  to end of the string.
     *  First position is 1.
     *  @param x  string expression
     *  @param from  start position 
     *  @return expression corresponding to substring extraction
     */	
    Expression<String> substring(Expression<String> x, int from);

    /**
     *  Substring extraction operation.
     *  Extracts a substring of given length starting at 
     *  specified position.
     *  First position is 1.
     *  @param x  string expression
     *  @param from  start position expression 
     *  @param len  length expression
     *  @return expression corresponding to substring extraction
     */		
    Expression<String> substring(Expression<String> x, Expression<Integer> from, Expression<Integer> len);
	
    /**
     *  Substring extraction operation.
     *  Extracts a substring of given length starting at 
     *  specified position.
     *  First position is 1.
     *  @param x  string expression
     *  @param from  start position 
     *  @param len  length
     *  @return expression corresponding to substring extraction
     */		
    Expression<String> substring(Expression<String> x, int from, int len);
	
    public static enum Trimspec { LEADING, TRAILING, BOTH }
	
    /**
     * Create expression to trim blanks from both ends of
     * a string.
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(Expression<String> x);
	
    /**
     * Create expression to trim blanks from a string.
     * @param ts  trim specification
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(Trimspec ts, Expression<String> x);

    /**
     * Create expression to trim character from both ends of
     * a string.
     * @param t  expression for character to be trimmed
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(Expression<Character> t, Expression<String> x);

    /**
     * Create expression to trim character from a string.
     * @param ts  trim specification
     * @param t  expression for character to be trimmed
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(Trimspec ts, Expression<Character> t, Expression<String> x);
	
    /**
     * Create expression to trim character from both ends of
     * a string.
     * @param t  character to be trimmed
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(char t, Expression<String> x);
	
    /**
     * Create expression to trim character from a string.
     * @param ts  trim specification
     * @param t  character to be trimmed
     * @param x  expression for string to trim
     * @return trim expression
     */
    Expression<String> trim(Trimspec ts, char t, Expression<String> x);
	
    /**
     * Create expression for lowercasing a string.
     * @param x  string expression
     * @return expression to convert to lowercase
     */
    Expression<String> lower(Expression<String> x);
	
    /**
     * Create expression for uppercasing a string.
     * @param x  string expression
     * @return expression to convert to uppercase
     */
    Expression<String> upper(Expression<String> x);
	
    /**
     * Create expression to return length of a string.
     * @param x  string expression
     * @return length expression
     */
    Expression<Integer> length(Expression<String> x);
	
	
    /**
     * Create expression to locate the position of one string
     * within another, returning position of first character
     * if found.
     * The first position in a string is denoted by 1.  If the
     * string to be located is not found, 0 is returned.
     * @param x  string to be search
     * @param pattern  string to be located
     * @return expression corresponding to position
     */
    Expression<Integer> locate(Expression<String> x, Expression<String> pattern);

    /**
     * Create expression to locate the position of one string
     * within another, returning position of first character
     * if found.
     * The first position in a string is denoted by 1.  If the
     * string to be located is not found, 0 is returned.
     * @param x  string to be search
     * @param pattern  string to be located
     * @param from  position at which to start search
     * @return expression corresponding to position
     */	
    Expression<Integer> locate(Expression<String> x, Expression<String> pattern, Expression<Integer> from);
	
    /**
     * Create expression to locate the position of one string
     * within another, returning position of first character
     * if found.
     * The first position in a string is denoted by 1.  If the
     * string to be located is not found, 0 is returned.
     * @param x  string to be search
     * @param pattern  string to be located
     * @return expression corresponding to position
     */
    Expression<Integer> locate(Expression<String> x, String pattern);

    /**
     * Create expression to locate the position of one string
     * within another, returning position of first character
     * if found.
     * The first position in a string is denoted by 1.  If the
     * string to be located is not found, 0 is returned.
     * @param x  string to be search
     * @param pattern  string to be located
     * @param from  position at which to start search
     * @return expression corresponding to position
     */	
    Expression<Integer> locate(Expression<String> x, String pattern, int from);
	

    // Date/time/timestamp functions:

    /**
     *  Create expression to return current date
     *  @return expression for current date
     */
    Expression<java.sql.Date> currentDate();

    /**
     *  Create expression to return current timestamp
     *  @return expression for current timestamp
     */	
    Expression<java.sql.Timestamp> currentTimestamp();

    /**
     *  Create expression to return current time
     *  @return expression for current time
     */	
    Expression<java.sql.Time> currentTime();
	

    //in builders:
	
    /**
     *  Interface used to build in-expressions.
     */
    public static interface In<T> extends Predicate {

		/**
		 * Returns the expression to be tested against the
		 * list of values.
		 * @return expression
		 */
		Expression<T> getExpression();
	
		/**
		 *  Add to list of values to be tested against.
		 *  @param value
		 *  @return in predicate
		 */
		In<T> value(T value);

		/**
		 *  Add to list of values to be tested against.
		 *  @param value expression
		 *  @return in predicate
		 */
		In<T> value(Expression<? extends T> value);
	}
	
    /**
     *  Create predicate to test whether given expression
     *  is contained in a list of values.
     *  @param  expression
     *  @return  in predicate
     */
    <T> In<T> in(Expression<? extends T> expression);
	

    //coalesce, nullif:
	
    /**
     * Create an expression that returns null if all its arguments
     * evaluate to null, and the value of the first non-null argument
     * otherwise.
     * @param x expression
     * @param y expression
     * @return expression corresponding to the given coalesce 
     * 				expression
     */
    <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y);

    /**
     * Create an expression that returns null if all its arguments
     * evaluate to null, and the value of the first non-null argument
     * otherwise.
     * @param x expression
     * @param y value
     * @return coalesce expression
     */
    <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y);
    
    /**
     * Create an expression that tests whether its argument are
     * equal, returning null if they are and the value of the
     * first expression if they are not.
     * @param x expression
     * @param y expression
     * @return expression corresponding to the given nullif 
     * 				expression
     */
    <Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y);

    
    /**
     * Create an expression that tests whether its argument are
     * equal, returning null if they are and the value of the
     * first expression if they are not.
     * @param x expression
     * @param y value
     * @return expression corresponding to the given nullif 
     * 				expression
     */
    <Y> Expression<Y> nullif(Expression<Y> x, Object y);


    // coalesce builder:

    /**
     *  Interface used to build coalesce expressions
     */
    public static interface Coalesce<T> extends Expression<T> {

		/**
		 * Add an argument to the coalesce expression.
		 * @param value
		 * @return coalesce expression
		 */
		Coalesce<T> value(T value);

		/**
		 * Add an argument to the coalesce expression.
		 * @param value expression
		 * @return coalesce expression
		 */
		Coalesce<T> value(Expression<? extends T> value);
	}
	
    /**
     * Create a coalesce expression.
     * A coalesce expression is equivalent to a case expression
     * that returns null if all its arguments evaluate to null,
     * and the value of its first non-null argument otherwise.
     * @return coalesce expression
     */
    <T> Coalesce<T> coalesce();


    //case builders:

    /**
     *  Interface used to build simple case expressions.
     */
    public static interface SimpleCase<C,R> extends Expression<R> {

		/**
		 * Returns the expression to be tested against the
		 * conditions.
		 * @return expression
		 */
		Expression<C> getExpression();

		/**
		 * Add a when/then clause to the case expression.
		 * @param condition  "then" condition
		 * @param result  "when" result
		 * @result simple case expression
		 */
		SimpleCase<C, R> when(C condition, R result);

		/**
		 * Add a when/then clause to the case expression.
		 * @param condition  "then" condition
		 * @param result  "when" result expression
		 * @result simple case expression
		 */
		SimpleCase<C, R> when(C condition, Expression<? extends R> result);

		/**
		 * Add an else clause to the case expression.
		 * @param result  "else" result
		 * @result expression
		 */
		Expression<R> elseCase(R result);

		/**
		 * Add an else clause to the case expression.
		 * @param result  "else" result expression
		 * @result expression
		 */
		Expression<R> elseCase(Expression<? extends R> result);
	}
	
    /**
     *  Create simple case expression.
     *  @param expression  to be tested against the cases
     *  @return simple case expression
     */
    <C, R> SimpleCase<C,R> selectCase(Expression<? extends C> expression);


    /**
     *  Interface used to build general case expressions.
     */
    public static interface Case<R> extends Expression<R> {

		/**
		 * Add a when/then clause to the case expression.
		 * @param condition  "then" condition
		 * @param result  "when" result
		 * @result general case expression
		 */
		Case<R> when(Expression<Boolean> condition, R result);

		/**
		 * Add a when/then clause to the case expression.
		 * @param condition  "then" condition
		 * @param result  "when" result expression
		 * @result general case expression
		 */
		Case<R> when(Expression<Boolean> condition, Expression<? extends R> result);

		/**
		 * Add an else clause to the case expression.
		 * @param result  "else" result
		 * @result expression
		 */
		Expression<R> elseCase(R result);

		/**
		 * Add an else clause to the case expression.
		 * @param result  "else" result expression
		 * @result expression
		 */
		Expression<R> elseCase(Expression<? extends R> result);
	}
	
    /**
     *  Create a general case expression.
     *  @return general case expression
     */
    <R> Case<R> selectCase();
}




