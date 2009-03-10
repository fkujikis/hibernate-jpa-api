package javax.persistence.criteria;

public interface Result {

    <X> X get(Selection<X> selection);

    <X> X get(int i, Class<X> type);

    Object get(int i);

    Object[] toArray();
}
