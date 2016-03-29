package abfab3d.param.editor;

import java.util.Vector;

import abfab3d.param.Parameter;

/**
 * Base code for all parameter editors
 *
 * @author Alan Hudson
 */
public abstract class BaseEditor implements Editor {

    protected Vector<ParamChangedListener> m_plisteners;
    protected Parameter m_param;

    public BaseEditor(Parameter param){
        m_param = param;
    }

    public Vector<ParamChangedListener> getParamChangedListeners(){
        return m_plisteners;
    }
    /**
     * Get notification of any parameter changes from this editor
     * @param listener
     */
    public void addParamChangedListener(ParamChangedListener listener) {
        if(m_plisteners == null)
            m_plisteners = new Vector<ParamChangedListener>();
        m_plisteners.add(listener);
    }

    public void addParamChangedListeners(Vector<ParamChangedListener> listeners) {
        if(listeners == null)
            return;
        for(int i = 0; i < listeners.size(); i++){
            addParamChangedListener(listeners.get(i));
        }
    }

    public void informParamChangedListeners(){

        if(m_plisteners != null){            
            for(int i = 0; i < m_plisteners.size(); i++){
                m_plisteners.get(i).paramChanged(m_param);
            }
        }
    }

}