DROP DATABASE IF EXISTS see_through;
CREATE DATABASE see_through;
USE see_through;

-- 구성원 테이블
CREATE TABLE members (
    member_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL DEFAULT '???',
    age INTEGER NOT NULL,
    image_path VARCHAR(255),
    preferred_foods JSON NOT NULL DEFAULT (JSON_ARRAY()),
    disliked_foods JSON NOT NULL DEFAULT (JSON_ARRAY()),
    is_registered BOOLEAN NOT NULL DEFAULT FALSE,
    recognition_times INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    PRIMARY KEY (member_id)
);

-- 냉장고 재료 테이블
CREATE TABLE ingredients (
    ingredient_id VARCHAR(36) NOT NULL,
    member_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    inbound_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration_at TIMESTAMP NULL,

    PRIMARY KEY (ingredient_id),
    FOREIGN KEY (member_id) REFERENCES members (member_id)
);

-- 냉장고 로그 테이블
CREATE TABLE ingredient_logs (
    ingredient_log_id INTEGER NOT NULL AUTO_INCREMENT,
    member_id VARCHAR(36) NOT NULL,
    ingredient_name VARCHAR(100) NOT NULL,
    movement_type ENUM('INBOUND', 'OUTBOUND') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (ingredient_log_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- 식단 테이블
CREATE TABLE meal_plans (
    meal_plan_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (meal_plan_id)
);

-- 식사 테이블
CREATE TABLE meals (
    meal_id VARCHAR(36) NOT NULL,
    dishes JSON NOT NULL,
    serving_time ENUM('BREAKFAST', 'LUNCH', 'DINNER') NOT NULL,

    PRIMARY KEY (meal_id)
);

-- 일일 식단 테이블
CREATE TABLE daily_meal_plans (
    daily_meal_plan_id VARCHAR(36) NOT NULL,
    meal_plan_id VARCHAR(36) NOT NULL,
    serving_date DATE NOT NULL,
    breakfast_meal_id VARCHAR(36),
    lunch_meal_id VARCHAR(36),
    dinner_meal_id VARCHAR(36),

    PRIMARY KEY (daily_meal_plan_id),
    UNIQUE(meal_plan_id, serving_date),
    FOREIGN KEY (meal_plan_id) REFERENCES meal_plans (meal_plan_id),
    FOREIGN KEY (breakfast_meal_id) REFERENCES meals (meal_id),
    FOREIGN KEY (lunch_meal_id) REFERENCES meals (meal_id),
    FOREIGN KEY (dinner_meal_id) REFERENCES meals (meal_id)
);

-- 식단 참여자 테이블
CREATE TABLE meal_plan_participations (
    member_id VARCHAR(36) NOT NULL,
    meal_plan_id VARCHAR(36) NOT NULL,

    PRIMARY KEY(member_id, meal_plan_id),
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (meal_plan_id) REFERENCES meal_plans (meal_plan_id)
);