'use strict';
const { Model } = require('sequelize');
module.exports = (sequelize, DataTypes) => {
    class User extends Model {
        /**
         * Helper method for defining associations.
         * This method is not a part of Sequelize lifecycle.
         * The `models/index` file will call this method automatically.
         */
        static associate(models) {
            // define association here
        }
    }
    User.init(
        {
            name: DataTypes.STRING,
            username: DataTypes.STRING,
            password: DataTypes.STRING,
            email: DataTypes.STRING,
            address: DataTypes.STRING,
            about: DataTypes.STRING,
            avatar: DataTypes.BLOB('long'),
            cookpadId: DataTypes.STRING,
            role: DataTypes.BOOLEAN,
        },
        {
            sequelize,
            modelName: 'User',
        }
    );
    return User;
};
