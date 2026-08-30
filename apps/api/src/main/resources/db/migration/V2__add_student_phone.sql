-- =============================================================================
-- SkillBridge — Add Student Phone Column Migration
-- Version: V2
-- Description: Add phone column to student_profiles table
-- =============================================================================

ALTER TABLE student_profiles
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30);
