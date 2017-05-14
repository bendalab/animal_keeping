/******************************************************************************
 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by jan on 27.12.16.

 *****************************************************************************/
package animalkeeping.model;

import animalkeeping.logging.ChangeLogInterface;
import animalkeeping.logging.ChangeLogUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Subject extends Entity implements ChangeLogInterface {
    private String name;
    private String alias;
    private Date birthday;
    private Gender gender;
    private SpeciesType speciesType;
    private SubjectType subjectType;
    private SupplierType supplier;
    private Set<Treatment> treatments =
            new HashSet<Treatment>(0);
    private Set<SubjectNote> notes =
            new HashSet<SubjectNote>(0);
    private Set<Housing> housings =
            new HashSet<Housing>(0);
    private Housing currentHousing;

    public Subject() {}

    public Subject(String name, SpeciesType speciesType, SubjectType subjectType) {
        this.name = name;
        this.speciesType = speciesType;
        this.subjectType = subjectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public SpeciesType getSpeciesType() {
        return speciesType;
    }

    public void setSpeciesType(SpeciesType speciesType) {
        this.speciesType = speciesType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SupplierType getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierType supplier) {
        this.supplier = supplier;
    }

    public Set<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Set<Housing> getHousings() {
        return housings;
    }

    public void setHousings(Set<Housing> housings) {
        this.housings = housings;
    }

    public Housing getCurrentHousing() {
        for (Housing h : getHousings()) {
            if (h.getEnd() == null) {
                return h;
            }
        }
        return null;
    }

    public Set<SubjectNote> getNotes() {
        return notes;
    }

    public void setNotes(Set<SubjectNote> notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Subject))
            return false;
        if (obj == this)
            return true;

        Subject rhs = (Subject) obj;
        return new EqualsBuilder().
                append(getName(), rhs.getName()).
                append(getSpeciesType().getName(), rhs.getSpeciesType().getName()).
                append(getId(), rhs.getId()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getId()).
                append(getName()).
                append(getSpeciesType().getName()).
                toHashCode();
    }


    @Override
    public String toString() {
        return "Subject{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", speciesType=" + speciesType.getName() +
                ", subjectType=" + subjectType.getName() +
                ", supplier=" + supplier.getName() +
                '}';
    }

    @Override
    public String getType(){
        return this.getClass().toString();
    }
}
