/*
 * Copyright (c) 2005-2020 Esito AS. All rights reserved.
 */
package no.esito.anonymizer.core;

public class ArrayKey {

    public String[] key;

    public ArrayKey(String[] key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayKey))
            return false;
        String[] other = ((ArrayKey) obj).key;
        if (key == other)
            return true;
        if (key == null || other == null)
            return false;
        if (key.length != other.length)
            return false;
        for (int i = 0; i < key.length; i++) {
            if (!key[i].equals(other[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int res = 0;
        if (key != null) {
            for (String k : key) {
                res += k.hashCode();
            }
        }
        return res;
    }
}