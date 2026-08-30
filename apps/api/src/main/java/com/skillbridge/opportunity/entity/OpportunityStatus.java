package com.skillbridge.opportunity.entity;

/**
 * Publication status of an opportunity.
 * Per OpenAPI contract: new postings are created as OPEN.
 * The DB schema also allows DRAFT but per the API contract DRAFT is not exposed.
 * Status can be toggled between OPEN and CLOSED by the owning company.
 */
public enum OpportunityStatus {
    DRAFT,
    OPEN,
    CLOSED
}
