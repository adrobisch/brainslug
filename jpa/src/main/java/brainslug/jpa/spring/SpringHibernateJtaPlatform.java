package brainslug.jpa.spring;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

public class SpringHibernateJtaPlatform extends AbstractJtaPlatform {
    private static TransactionManager sTransactionManager;
    private static UserTransaction sUserTransaction;

    @Override
    protected TransactionManager locateTransactionManager() {
        Assert.notNull(sTransactionManager, "TransactionManager is not set");
        return sTransactionManager;
    }


    @Override
    protected UserTransaction locateUserTransaction() {
        Assert.notNull(sUserTransaction, "UserTransaction is not set");
        return sUserTransaction;
    }

    public static void setJtaTransactionManager(JtaTransactionManager jtaTransactionManager) {
        sTransactionManager = jtaTransactionManager.getTransactionManager();
        sUserTransaction = jtaTransactionManager.getUserTransaction();
    }
}
