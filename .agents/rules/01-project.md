---
trigger: always_on
---

# SkillBridge — Project Rules

## Project Identity

Product:
SkillBridge

Problem Statement:
SIH26044 — Portal for Academia–Industry Collaboration
for Skill Mapping, Internships and Placement.

## Product Purpose

SkillBridge connects:
Student Skills
↔
Industry Requirements
↔
College Skill Insights

## Primary Roles

1. Student
2. Company
3. College
4. Admin

## Core Product Loop

Student Skills
→ Company Requirements
→ Skill Matching
→ Matched/Missing Skills
→ Opportunity
→ Application
→ Recruitment
→ Internship/Placement
→ Company Feedback
→ College Skill-Gap Analysis

## MVP Rule

The Must-Have features in the approved SRS
are the MVP source of truth.

Do not add features outside the approved scope
without explicit user approval.

## Important Product Rules

- Skill match measures skill coverage, not proficiency.
- Match score is based on matched required skills.
- Eligibility is separate from skill matching.
- College skill-gap analysis is institution-level.
- AI is optional and never a dependency for core functionality.
- Do not turn SkillBridge into an LMS, social network,
  generic job board, or assessment platform.

## Source of Truth

When implementing functionality, consult:

@docs/PRD.md
@docs/SRS.md

If a requirement conflicts with these documents,
STOP and ask for clarification.

Never silently invent requirements.